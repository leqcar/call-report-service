package org.efire.net.callreportservice.job;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Created by DELFIN.TENERIFE on 7/31/2017.
 */
public class MonitorJobCondition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        return conditionContext.getEnvironment().getProperty("mode").equalsIgnoreCase("monitor");
    }
}
