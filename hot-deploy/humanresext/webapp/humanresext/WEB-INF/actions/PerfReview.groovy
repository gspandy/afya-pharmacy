 import org.ofbiz.entity.*
 import org.ofbiz.entity.condition.*
 import org.ofbiz.entity.util.*
 import org.ofbiz.base.util.*
 import org.ofbiz.base.util.collections.*

 
 parameters.each{
	 
	 parameter->System.out.println(parameter.key + parameter.value);
	 
 }