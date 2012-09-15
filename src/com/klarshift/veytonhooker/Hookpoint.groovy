package com.klarshift.veytonhooker

class Hookpoint {
	String key
	File file
	int line
	String type
	
	public String toString(){
		String o = "Line $line\t"
		if(line < 100)o += "\t"
		o += key
		return o
	}
}
