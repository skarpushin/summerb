package org.summerb.approaches.validation;

import java.util.List;

public interface ObjectValidator<T> {
	void validate(T subject, String fieldToken, ValidationContext ctx, List<T> optionalSubjectCollection,
			ValidationContext parentCtx);
}
