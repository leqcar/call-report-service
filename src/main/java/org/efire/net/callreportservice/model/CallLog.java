package org.efire.net.callreportservice.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by jongtenerife on 26/07/2017.
 */
@NoArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class CallLog {

    private LocalDate callDate;
    private SourceId sourceId;
    private List<CallDuration> callDurations;

}
