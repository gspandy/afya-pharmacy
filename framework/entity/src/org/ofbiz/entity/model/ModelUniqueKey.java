package org.ofbiz.entity.model;

import java.util.ArrayList;
import java.util.List;

import org.ofbiz.base.util.UtilXml;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ModelUniqueKey {

    protected List<ModelKeyMap> keyMaps = new ArrayList<ModelKeyMap>();
    
    /** the main entity of this relation */
    protected ModelEntity mainEntity = null;

	private final String uniqueName;


	public ModelUniqueKey(ModelEntity mainEntity, Element relationElement) {
    this.mainEntity = mainEntity;

    this.uniqueName = UtilXml.checkEmpty(relationElement.getAttribute("title")).intern();

    NodeList keyMapList = relationElement.getElementsByTagName("unique-key-map");
    for (int i = 0; i < keyMapList.getLength(); i++) {
        Element keyMapElement = (Element) keyMapList.item(i);

        if (keyMapElement.getParentNode() == relationElement) {
            ModelKeyMap keyMap = new ModelKeyMap(keyMapElement);

            if (keyMap != null) {
                this.keyMaps.add(keyMap);
            }
        }
    }
}


	public List<ModelKeyMap> getKeyMaps() {
	return keyMaps;
	}


	public void setKeyMaps(List<ModelKeyMap> keyMaps) {
	this.keyMaps = keyMaps;
	}


	public ModelEntity getMainEntity() {
	return mainEntity;
	}


	public void setMainEntity(ModelEntity mainEntity) {
	this.mainEntity = mainEntity;
	}


	public String getUniqueName() {
	return uniqueName;
	}


	public void setModelEntity(ModelEntity modelEntity) {
	this.mainEntity=modelEntity;
	}

}
