package com.ndz.component;

//import org.apache.velocity.util.StringUtils;
import org.zkoss.zul.Label;

public class HrmsLabel extends Label {
	@Override
	public String getValue() {
		String _value = super.getValue();
		if (("Comment".equals(_value))||("comment".equals(_value))) {
			_value = "Comments";
			return _value;
		}
		if (("Admin Comment".equals(_value))||("Admin comment".equals(_value))) {
			_value = "Admin Comments";
			return _value;
		}
		if (("Manager Comment".equals(_value))||("Manager comment".equals(_value))) {
			_value = "Manager Comments";
			return _value;
		}
		//if (_value != null) {
			//_value = _value.toLowerCase();
			//if(_value.length()>1)
			//_value = StringUtils.capitalizeFirstLetter(_value);
		//}
		return _value;
	}
	private static final long serialVersionUID = 1L;
}
