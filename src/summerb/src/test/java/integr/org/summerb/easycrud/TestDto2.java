package integr.org.summerb.easycrud;

import java.io.Serializable;

import org.summerb.easycrud.api.dto.HasAuthor;
import org.summerb.easycrud.api.dto.HasAutoincrementId;
import org.summerb.easycrud.api.dto.HasTimestamps;

public class TestDto2 implements HasAutoincrementId, HasAuthor, HasTimestamps, Serializable {
	private static final long serialVersionUID = -2954623750074589334L;

	private Long id;
	private String env;
	private boolean active;
	private int majorVersion;
	private int minorVersion;
	private long createdAt;
	private long modifiedAt;
	private String createdBy;
	private String modifiedBy;
	private String linkToFullDonwload;
	private String linkToPatchToNextVersion;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getMajorVersion() {
		return majorVersion;
	}

	public void setMajorVersion(int majorVersion) {
		this.majorVersion = majorVersion;
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	public void setMinorVersion(int minorVersion) {
		this.minorVersion = minorVersion;
	}

	@Override
	public long getCreatedAt() {
		return createdAt;
	}

	@Override
	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public long getModifiedAt() {
		return modifiedAt;
	}

	@Override
	public void setModifiedAt(long modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	@Override
	public String getCreatedBy() {
		return createdBy;
	}

	@Override
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@Override
	public String getModifiedBy() {
		return modifiedBy;
	}

	@Override
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getLinkToFullDonwload() {
		return linkToFullDonwload;
	}

	public void setLinkToFullDonwload(String linkToFullDonwload) {
		this.linkToFullDonwload = linkToFullDonwload;
	}

	public String getLinkToPatchToNextVersion() {
		return linkToPatchToNextVersion;
	}

	public void setLinkToPatchToNextVersion(String linkToPatchToNextVersion) {
		this.linkToPatchToNextVersion = linkToPatchToNextVersion;
	}
}
