package org.summerb.microservices.emailsender.api.dto;

public class EmailTemplateParamsFactory {
	private EmailTemplateParamsFactory() {
	}

	public static class SimpleUser {
		private String name;

		public SimpleUser(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public static class SimpleMsgBody {
		private final String msg;
		private final String id;

		public SimpleMsgBody(String msg, String id) {
			this.msg = msg;
			this.id = id;
		}

		public String getMsg() {
			return msg;
		}

		public String getId() {
			return id;
		}
	}

	public static EmailTemplateParams createEmailTemplateParams() {
		return new EmailTemplateParams(new SimpleUser("actualSender"), new SimpleUser("actualRecipient"),
				new SimpleMsgBody("bodyMsg", "bodyId"));
	}

}