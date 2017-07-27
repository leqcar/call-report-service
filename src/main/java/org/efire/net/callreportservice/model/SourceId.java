package org.efire.net.callreportservice.model;

import lombok.Getter;

/**
 * Created by jongtenerife on 26/07/2017.
 */
@Getter
public class SourceId {

    private Integer sourceNumber;

    public SourceId(Integer sourceNumber) {
        this.sourceNumber = sourceNumber;
    }
}
