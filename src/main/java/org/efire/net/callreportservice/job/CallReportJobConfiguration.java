package org.efire.net.callreportservice.job;

import org.efire.net.callreportservice.model.InputDTO;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.ItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;

/**
 * Created by jongtenerife on 26/07/2017.
 */
@Configuration
@EnableBatchProcessing
public class CallReportJobConfiguration {

    public static final String INPUT_FILE = "input-call_Min.csv";

    @Bean
    public Job callReportJob(JobBuilderFactory jbf) {

        return jbf.get("callReportJob")
                .start(inputFileToDBStep(null))
                .next(sourceCallDurationStep(null))
                .build();
    }

    @Bean
    public Step inputFileToDBStep(StepBuilderFactory sbf) {
        return sbf.get("inputFileStep")
                .<InputDTO, InputDTO>chunk(1000)
                .reader(inputFileReader())
                .writer(jdbcCallLogWriter(null))
                .build();
    }

    @Bean
    public Step sourceCallDurationStep(StepBuilderFactory sbf) {
        return sbf.get("sourceCallDurationStep")
                .<SourceCallLog, SourceCallLog>chunk(1000)
                .reader(callLogBySourceReader(null))
                .processor(new PassThroughItemProcessor<>())
                .writer(dummyWriter())
                .build();
    }

    @Bean
    public ItemReader<? extends SourceCallLog> callLogBySourceReader(DataSource ds) {
        JdbcCursorItemReader reader = new JdbcCursorItemReader();
        reader.setDataSource(ds);
        reader.setRowMapper(new RowMapper() {
            @Override
            public Object mapRow(ResultSet rs, int i) throws SQLException {
                SourceCallLog sourceCallLog = new SourceCallLog();
                sourceCallLog.setSource(rs.getInt("SOURCE"));
                sourceCallLog.setHours(rs.getInt("HH"));
                sourceCallLog.setMinutes(rs.getInt("MM"));
                sourceCallLog.setSeconds(rs.getInt("SS"));
                return sourceCallLog;
            }
        });
        reader.setSql("SELECT RPT_DATE, SOURCE, SUM(HH) as HH, SUM(MM) as MM, SUM(SS) as SS " +
                " FROM CALL_LOGS " +
                " GROUP BY RPT_DATE, SOURCE " +
                " ORDER BY SOURCE");
        return reader;

    }

    @Bean
    public ItemProcessor<SourceCallLog, SourceCallLog> callLogProcessor() {
        return new CallLogProcessor();
    }

    @Bean
    public ItemWriter<SourceCallLog> dummyWriter() {
        return new DummyWriter();
    }
    @Bean
    public ItemReader<InputDTO> inputFileReader() {
        FlatFileItemReader<InputDTO> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource(INPUT_FILE));
        reader.setLinesToSkip(1);
        reader.setLineMapper(inputDTOLineMapper());
        reader.setEncoding(StandardCharsets.UTF_8.toString());
        return reader;
    }

    private LineMapper<InputDTO> inputDTOLineMapper() {
        DefaultLineMapper<InputDTO> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(new String[]{ "Date", "Time", "Source", "Destination", "Duration" });
        lineTokenizer.setIncludedFields(new int[]{0,1,2,3,4});

        BeanWrapperFieldSetMapper<InputDTO> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(InputDTO.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(inputDTOFieldSetMapper());

        return lineMapper;
    }

    @Bean
    public FieldSetMapper<InputDTO> inputDTOFieldSetMapper() {
        return new InputDTOFieldSetMapper();
    }

    @Bean
    public ItemWriter<InputDTO> jdbcCallLogWriter(DataSource ds) {
        JdbcBatchItemWriter<InputDTO> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(ds);
        writer.setSql("INSERT INTO CALL_LOGS (RPT_DATE,CALL_TIME,SOURCE,DESTINATION,HH, MM, SS) " +
                "VALUES (:callDate, :callTime, :source, :destination, :hours, :minutes, :seconds)");
        writer.setItemSqlParameterSourceProvider(new ItemSqlParameterSourceProvider<InputDTO>() {
            @Override
            public SqlParameterSource createSqlParameterSource(InputDTO inputDTO) {
                return new BeanPropertySqlParameterSource(inputDTO);
            }
        });
        return writer;
    }

}
