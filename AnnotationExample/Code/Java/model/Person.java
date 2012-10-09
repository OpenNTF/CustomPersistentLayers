package model;

import com.ibm.xsp.model.DataObject;

public class Person implements DataObject {
	String name="weihang";
	public Class<?> getType(Object arg0) {
		// TODO Auto-generated method stub
		System.out.println("KKKK");
		return null;
	}

	public Object getValue(Object arg0) {
		// TODO Auto-generated method stub
		System.out.println("KKKK");
		return null;
	}

	public boolean isReadOnly(Object arg0) {
		System.out.println("KKKK");
		// TODO Auto-generated method stub
		return false;
	}

	public void setValue(Object arg0, Object arg1) {
		
		System.out.println("KKKK");
		// TODO Auto-generated method stub
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
