package com.realestate.due_diligence_agent.service;

import com.realestate.due_diligence_agent.dto.RiskAssessmentResult;
import com.realestate.due_diligence_agent.entity.Property;

public interface RiskCategoryStrategy {
    RiskAssessmentResult assess(Property property);
}
