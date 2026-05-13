package com.yupi.yuoj.model.dto.bazi;

import java.io.Serializable;
import lombok.Data;

/**
 * Request for Bazi relationship analysis.
 */
@Data
public class BaziRelationAnalyzeRequest implements Serializable {

    /**
     * Current city.
     */
    private String currentCity;

    /**
     * single / dating / married / divorced / complicated / unknown
     */
    private String relationshipStatus;

    /**
     * student / starting / growing / stable / entrepreneur / management / unknown
     */
    private String careerStage;

    /**
     * What the user most wants to know.
     */
    private String focusQuestion;

    private static final long serialVersionUID = 1L;
}
