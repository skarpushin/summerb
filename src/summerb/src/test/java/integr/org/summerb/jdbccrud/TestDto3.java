package integr.org.summerb.jdbccrud;

import java.io.Serializable;

import org.summerb.approaches.jdbccrud.api.dto.HasUuid;

public class TestDto3 implements HasUuid, Serializable {
	private static final long serialVersionUID = 2232705400887262676L;
	
	private String id;
	private String linkToDtoOneOptional;
	private long linkToDtoTwo;
	private Long linkToDtoTwoOptional;
	private String linkToSelfOptional;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public String getLinkToDtoOneOptional() {
		return linkToDtoOneOptional;
	}

	public void setLinkToDtoOneOptional(String linkToDto1Optional) {
		this.linkToDtoOneOptional = linkToDto1Optional;
	}

	public long getLinkToDtoTwo() {
		return linkToDtoTwo;
	}

	public void setLinkToDtoTwo(long linkToDto2) {
		this.linkToDtoTwo = linkToDto2;
	}

	public Long getLinkToDtoTwoOptional() {
		return linkToDtoTwoOptional;
	}

	public void setLinkToDtoTwoOptional(Long linkToDto2Optional) {
		this.linkToDtoTwoOptional = linkToDto2Optional;
	}

	public String getLinkToSelfOptional() {
		return linkToSelfOptional;
	}

	public void setLinkToSelfOptional(String linkToSelfOptional) {
		this.linkToSelfOptional = linkToSelfOptional;
	}

}
