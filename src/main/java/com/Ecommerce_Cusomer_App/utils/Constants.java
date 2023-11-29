package com.Ecommerce_Cusomer_App.utils;

public class Constants {

	public enum UserRole {
		ROLE_CUSTOMER("Customer"), ROLE_ADMIN("Admin");

		private String role;

		private UserRole(String role) {
			this.role = role;
		}

		public String value() {
			return this.role;
		}
	}

	public enum CategoryStatus {
		ACTIVE("Active"), DEACTIVATED("Deactivated");

		private String status;

		private CategoryStatus(String status) {
			this.status = status;
		}

		public String value() {
			return this.status;
		}
	}

	public enum SubCategoryStatus {
		ACTIVE("Active"), DEACTIVATED("Deactivated");

		private String status;

		SubCategoryStatus(String status) {
			this.status = status;
		}

		public String value() {
			return this.status;
		}
	}

	public enum AdminStatus {
		ACTIVE("Active"), DEACTIVATED("Deactivated");

		private String status;

		AdminStatus(String status) {
			this.status = status;
		}

		public String value() {
			return this.status;
		}
	}

	public enum UserStatus {
		ACTIVE("Active"), DEACTIVATED("Deactivated");

		private String status;

		UserStatus(String status) {
			this.status = status;
		}

		public String value() {
			return this.status;
		}
	}

	public enum LocationStatus {
		ACTIVE("Active"), DEACTIVATED("Deactivated");

		private String status;

		private LocationStatus(String status) {
			this.status = status;
		}

		public String value() {
			return this.status;
		}
	}

}
