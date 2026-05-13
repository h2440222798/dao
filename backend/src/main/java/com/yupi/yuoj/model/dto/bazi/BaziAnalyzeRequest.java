package com.yupi.yuoj.model.dto.bazi;

import java.io.Serializable;
import lombok.Data;

/**
 * Request for personal Bazi and constitution analysis.
 */
@Data
public class BaziAnalyzeRequest implements Serializable {

    /**
     * male / female / unknown.
     */
    private String gender;

    /**
     * Birth date, yyyy-MM-dd.
     */
    private String birthDate;

    /**
     * Birth time, HH:mm.
     */
    private String birthTime;

    /**
     * Birth place or current city.
     */
    private String birthPlace;

    /**
     * Whether the user wants true solar time correction.
     */
    private Boolean useTrueSolarTime;

    /**
     * solar only for now.
     */
    private String calendarType;

    /**
     * Optional symptom text to guide constitution analysis.
     */
    private String bodySignals;

    private static final long serialVersionUID = 1L;
}
