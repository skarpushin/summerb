package org.summerb.approaches.jdbccrud.api.dto.relations;

import java.beans.Transient;
import java.io.Serializable;

import org.summerb.approaches.jdbccrud.api.EasyCrudService;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * That dto describes reference from one object to another. It's designed to
 * describe only straightforward references.
 * 
 * References are named so they could be used in data load plans by name.
 * 
 * In case this is many-to-many reference then {@link #m2mEntity} must describe
 * m2m table.
 * 
 * @author sergeyk
 *
 */
public class Ref implements Serializable {
	private static final String SUFFIX_BACK = "_back";

	private static final long serialVersionUID = 1133870870016615282L;

	private String name;

	private String fromEntity;
	private String fromField;

	private String toEntity;
	private String toField;

	private RelationType relationType;
	private RefQuantity quantity;

	private String m2mEntity;

	public Ref() {

	}

	public Ref(String name, String fromEntity, String fromField, String toEntity, String toField,
			RelationType relationType, RefQuantity quantity) {
		this.name = name;
		this.fromEntity = fromEntity;
		this.fromField = fromField;
		this.toEntity = toEntity;
		this.toField = toField;
		this.relationType = relationType;
		this.quantity = quantity;
	}

	/**
	 * @return returns reversed reference
	 * @deprecated avoid using it since reverse is not 100% implemented, doesn't
	 *             cover all cases of {@link #relationType}
	 */
	@Deprecated
	public Ref reverse() {
		Ref ret = new Ref();
		ret.setName(!name.endsWith(SUFFIX_BACK) ? name + SUFFIX_BACK
				: name.substring(0, name.length() - SUFFIX_BACK.length()));
		ret.setFromEntity(getToEntity());
		ret.setFromField(getToField());
		ret.setToEntity(getFromEntity());
		ret.setToField(getFromField());

		// NOT sure how to set relation type
		// TODO: Impl

		// quantity
		if (quantity == RefQuantity.Many2Many || quantity == RefQuantity.One2One) {
			ret.setQuantity(quantity);
		} else if (quantity == RefQuantity.One2Many) {
			ret.setQuantity(RefQuantity.Many2One);
		} else {
			ret.setQuantity(RefQuantity.One2Many);
		}

		return ret;
	}

	/**
	 * @return Reference name, must be unique within application
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Transient
	@JsonIgnore
	public boolean isManyToOne() {
		return quantity == RefQuantity.Many2One;
	}

	@Transient
	@JsonIgnore
	public boolean isOneToOne() {
		return quantity == RefQuantity.One2One;
	}

	@Transient
	@JsonIgnore
	public boolean isOneToMany() {
		return quantity == RefQuantity.One2Many;
	}

	@Transient
	@JsonIgnore
	public boolean isManyToMany() {
		return quantity == RefQuantity.Many2Many;
	}

	/**
	 * @return entity type message code See
	 *         {@link EasyCrudService#getEntityTypeMessageCode()}
	 */
	public String getFromEntity() {
		return fromEntity;
	}

	public void setFromEntity(String fromEntity) {
		this.fromEntity = fromEntity;
	}

	public String getFromField() {
		return fromField;
	}

	public void setFromField(String fromField) {
		this.fromField = fromField;
	}

	/**
	 * @return entity type message code See
	 *         {@link EasyCrudService#getEntityTypeMessageCode()}
	 */
	public String getToEntity() {
		return toEntity;
	}

	public void setToEntity(String toEntity) {
		this.toEntity = toEntity;
	}

	public String getToField() {
		return toField;
	}

	public void setToField(String toField) {
		this.toField = toField;
	}

	/**
	 * @return relation type that supposed to help application to handle cascade
	 *         changes automaticall. Reserved for future used, not actually
	 *         handled/implemented
	 * 
	 * @deprecated incubating functionality, davoid using
	 */
	@Deprecated
	public RelationType getRelationType() {
		return relationType;
	}

	public void setRelationType(RelationType relationType) {
		this.relationType = relationType;
	}

	public RefQuantity getQuantity() {
		return quantity;
	}

	public void setQuantity(RefQuantity quantity) {
		this.quantity = quantity;
	}

	public String getM2mEntity() {
		if (m2mEntity == null && quantity == RefQuantity.Many2Many) {
			m2mEntity = buildDefaultM2mEntityName(fromEntity, toEntity);
		}
		return m2mEntity;
	}

	public static String buildDefaultM2mEntityName(String fromEntity2, String toEntity2) {
		return fromEntity2 + ".to." + toEntity2;
	}

	public void setM2mEntity(String m2mEntity) {
		this.m2mEntity = m2mEntity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fromEntity == null) ? 0 : fromEntity.hashCode());
		result = prime * result + ((fromField == null) ? 0 : fromField.hashCode());
		result = prime * result + ((m2mEntity == null) ? 0 : m2mEntity.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((quantity == null) ? 0 : quantity.hashCode());
		result = prime * result + ((relationType == null) ? 0 : relationType.hashCode());
		result = prime * result + ((toEntity == null) ? 0 : toEntity.hashCode());
		result = prime * result + ((toField == null) ? 0 : toField.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Ref other = (Ref) obj;
		if (fromEntity == null) {
			if (other.fromEntity != null) {
				return false;
			}
		} else if (!fromEntity.equals(other.fromEntity)) {
			return false;
		}
		if (fromField == null) {
			if (other.fromField != null) {
				return false;
			}
		} else if (!fromField.equals(other.fromField)) {
			return false;
		}
		if (m2mEntity == null) {
			if (other.m2mEntity != null) {
				return false;
			}
		} else if (!m2mEntity.equals(other.m2mEntity)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (quantity != other.quantity) {
			return false;
		}
		if (relationType != other.relationType) {
			return false;
		}
		if (toEntity == null) {
			if (other.toEntity != null) {
				return false;
			}
		} else if (!toEntity.equals(other.toEntity)) {
			return false;
		}
		if (toField == null) {
			if (other.toField != null) {
				return false;
			}
		} else if (!toField.equals(other.toField)) {
			return false;
		}
		return true;
	}

}