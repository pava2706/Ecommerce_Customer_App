package com.Ecommerce_Cusomer_App.utils;

public class Constants {

	public enum CategoryStatus {
		ACTIVE("Active"),
		DEACTIVATED("Deactivated");
		
		
		private String status;

	    private CategoryStatus(String status) {
	      this.status = status;
	    }

	    public String value() {
	      return this.status;
	    }    
	}
	
	public enum SubCategoryStatus {
		ACTIVE("Active"),
		DEACTIVATED("Deactivated");
		
		
		private String status;

	    SubCategoryStatus(String status) {
	    	this.status = status;
		}

	    public String value() {
	      return this.status;
	    }    
	}
	
}
