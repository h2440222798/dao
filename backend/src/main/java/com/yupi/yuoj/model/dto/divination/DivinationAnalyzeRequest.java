package com.yupi.yuoj.model.dto.divination;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * Request for daily lot drawing and Liuyao divination.
 */
@Data
public class DivinationAnalyzeRequest implements Serializable {

    /**
     * Optional concrete event/question for Liuyao interpretation.
     */
    private String question;

    /**
     * Current date text from frontend.
     */
    private String date;

    /**
     * Lot number, 1-64.
     */
    private Integer signNo;

    /**
     * Frontend generated lot title.
     */
    private String signTitle;

    /**
     * Lot level text.
     */
    private String signLevel;

    /**
     * Six Yao values from bottom to top: 6 old yin, 7 young yang, 8 young yin, 9 old yang.
     */
    private List<Integer> lines;

    /**
     * Original hexagram name.
     */
    private String hexagramName;

    /**
     * Changed hexagram name.
     */
    private String changedHexagramName;

    /**
     * Lower trigram.
     */
    private String lowerTrigram;

    /**
     * Upper trigram.
     */
    private String upperTrigram;

    /**
     * Changed lower trigram.
     */
    private String changedLowerTrigram;

    /**
     * Changed upper trigram.
     */
    private String changedUpperTrigram;

    /**
     * Moving line numbers, 1-6 from bottom to top.
     */
    private List<Integer> movingLines;

    /**
     * Optional user context.
     */
    private String extraInfo;

    private static final long serialVersionUID = 1L;
}
