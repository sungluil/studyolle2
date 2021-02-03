package com.studyolle.modules.account.form;

import com.studyolle.modules.zone.Zone;

import lombok.Data;

@Data
public class ZoneForm {

	private String zoneName;
	
    public String getCityName() {
    	System.out.println("city : "+zoneName.substring(0, zoneName.indexOf("(")));
        return zoneName.substring(0, zoneName.indexOf("("));
    }

    public String getProvinceName() {
    	System.out.println("Province : "+zoneName.substring(zoneName.indexOf("/") + 1));
        return zoneName.substring(zoneName.indexOf("/") + 1);
    }

    public String getLocalNameOfCity() {
    	System.out.println("LocalName : "+zoneName.substring(zoneName.indexOf("(") + 1, zoneName.indexOf(")")));
        return zoneName.substring(zoneName.indexOf("(") + 1, zoneName.indexOf(")"));
    }

    public Zone getZone() {
    	System.out.println("실행");
        return Zone.builder().city(this.getCityName())
                .localNameOfCity(this.getLocalNameOfCity())
                .province(this.getProvinceName()).build();
    }
    
}
