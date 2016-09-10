package org.summerb.approaches.jdbccrud.api.dto.relations;

public enum RelationType {
	/**
	 * That type of reference is used when Source just references Target (i.e.
	 * we just referencing dictionary item). If Source is deleted that will not
	 * affect Target.
	 */
	References,

	/**
	 * That means the Source contains Target (like tree holds leafs). Target is
	 * meaningless without Source. So if Source is deleted then same affects
	 * Target
	 */
	Aggregates,

	/**
	 * Opposite to 'Aggregates'
	 */
	PartOf,

	/**
	 * That means that Source is actually is a part of Target, but target is not
	 * aware of exact Source. In that case typically multiple types of Sources
	 * are referenced to same type of Target (i.e.: Audit log, ACL, etc...).
	 */
	Aspect;
}
