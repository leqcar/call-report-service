package org.efire.net.callreportservice.job;

import org.efire.net.callreportservice.model.CallLog;
import org.efire.net.callreportservice.model.CallReport;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;

import javax.sql.DataSource;
import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * Created by DELFIN.TENERIFE on 7/31/2017.
 */
@Configuration
@Conditional(CallDetailRecordCondition.class)
@EnableBatchProcessing
public class CallDetailRecordConfiguration {

    @Value("${file.location.input}")
    private String inputFile;

    private static final String INPUT_FILE = "input-call-min-cdr.csv";

    @Bean
    public Job callDetailJob(JobBuilderFactory jbf) {
        return jbf.get("callDetailJob")
                .start(callDetailFileToDBStep(null))
                .next(callDetailReportStep(null))
                .build();
    }

    @Bean
    public Step callDetailFileToDBStep(StepBuilderFactory sbf) {
        return sbf.get("callDetailFileToDBStep")
                .<CallLog, CallLog>chunk(1000)
                .reader(inputFileReader())
                .writer(jdbcCallLogWriter(null))
                .build();
    }

    @Bean
    public ItemReader<CallLog> inputFileReader() {
        FlatFileItemReader<CallLog> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource(INPUT_FILE)); //-->used to test using csv file in the classpath
       // reader.setResource(new FileSystemResource(new File(inputFile)));
        reader.setLinesToSkip(1);
        reader.setLineMapper(callDetailRecordLineMapper());
        reader.setEncoding(StandardCharsets.UTF_8.toString());
        return reader;
    }

    private LineMapper<CallLog> callDetailRecordLineMapper() {
        DefaultLineMapper<CallLog> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(new String[]{ "Date", "Source", "Destination", "Duration" });
        lineTokenizer.setIncludedFields(new int[]{0,1,3,8});
        BeanWrapperFieldSetMapper<CallLog> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(CallLog.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(callDetailRecordFieldSetMapper());
        return lineMapper;
    }

    @Bean
    public FieldSetMapper<CallLog> callDetailRecordFieldSetMapper() {
        return new CdrFieldSetMapper();
    }

    @Bean
    public ItemWriter<CallLog> jdbcCallLogWriter(DataSource ds) {
        JdbcBatchItemWriter<CallLog> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(ds);
        writer.setSql("INSERT INTO CALL_LOGS (RPT_DATE,CALL_TIME,SOURCE,DESTINATION,HH, MM, SS) " +
                "VALUES (:callDate, :callTime, :source, :destination, :hours, :minutes, :seconds)");
        writer.setItemSqlParameterSourceProvider(callLog -> new BeanPropertySqlParameterSource(callLog));
        return writer;
    }

    @Bean
    public Step callDetailReportStep(StepBuilderFactory sbf) {
        return sbf.get("callDetailReportStep")
                .<CallReport, CallReport>chunk(1000)
                .reader(callLogBySourceReader(null))
                .processor(callLogProcessor())
                .writer(reportOutputWriter(null))
                .build();
    }

    @Bean
    public ItemProcessor<CallReport, CallReport> callLogProcessor() {
        return new CallLogProcessor();
    }

    @Bean
    public ItemReader<? extends CallReport> callLogBySourceReader(DataSource ds) {
        JdbcCursorItemReader reader = new JdbcCursorItemReader();
        reader.setDataSource(ds);
        reader.setRowMapper((rs, i) -> {
            CallReport sourceCallLog = new CallReport();
            sourceCallLog.setReportDate(rs.getDate("RPT_DATE"));
            sourceCallLog.setSource(rs.getString("SOURCE"));
            sourceCallLog.setHours(rs.getInt("HH"));
            sourceCallLog.setMinutes(rs.getInt("MM"));
            sourceCallLog.setSeconds(rs.getInt("SS"));
            return sourceCallLog;
        });
        reader.setSql("SELECT RPT_DATE, SOURCE, SUM(HH) as HH, SUM(MM) as MM, SUM(SS) as SS " +
                " FROM CALL_LOGS " +
                " GROUP BY RPT_DATE, SOURCE " +
                " ORDER BY RPT_DATE DESC, SOURCE");
        return reader;

    }
    @Bean
    public ItemWriter<CallReport> reportOutputWriter(@Value("${file.location.output-cdr}") String outputPath) {
        FlatFileItemWriter<CallReport> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource(new File(outputPath)));
        writer.setEncoding(StandardCharsets.UTF_8.toString());
        writer.setLineAggregator(reportOutputLineAggregator());
        writer.setHeaderCallback(w -> w.write("Report Date, Source, Total Duration"));
        return writer;
    }

    private LineAggregator<CallReport> reportOutputLineAggregator() {
        DelimitedLineAggregator lineAggregator = new DelimitedLineAggregator();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(new BeanWrapperFieldExtractor() {
            {
                setNames(new String[]{"reportDate","source", "duration"});
            }
        });
        return  lineAggregator;
    }
}
