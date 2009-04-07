/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Feb 12, 2009
 * 
 * Copyright 2009 Javassonne Team
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software 
 *  distributed under the License is distributed on an "AS IS" BASIS, 
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 *  implied. See the License for the specific language governing 
 *  permissions and limitations under the License. 
 */

package org.javassonne.model;

import java.util.ArrayList;
import java.util.HashMap;

public class TileFeatureBindings {

	HashMap<String, ArrayList<String>> bindings_ = new HashMap<String, ArrayList<String>>();
	HashMap<String, String> completionMultipliers_ = new HashMap<String, String>();
	
	public TileFeatureBindings() {
	}

	public Integer completionMultiplierForFeature(String identifier)
	{
		return Integer.parseInt(completionMultipliers_.get(identifier));
	}
	
	public Boolean featuresBind(String identifier1, String identifier2) {
		if (bindings_.containsKey(identifier1))
			return bindings_.get(identifier1).contains(identifier2);
		else
			return false;
	}

	public Boolean featuresBind(TileFeature feature1, TileFeature feature2) {
		if (feature1 == null && feature2 == null)
			return true;
		else if (feature1 == null || feature2 == null)
			return false;
		else if (bindings_.containsKey(feature1.identifier))
			return bindings_.get(feature1.identifier).contains(
					feature2.identifier);
		else
			return false;
	}

	public void addFeatureBinding(String identifier1, String identifier2) {
		if (bindings_.containsKey(identifier1) == false)
			bindings_.put(identifier1, new ArrayList<String>());
		if (bindings_.containsKey(identifier2) == false)
			bindings_.put(identifier2, new ArrayList<String>());

		bindings_.get(identifier1).add(identifier2);
		if (identifier1.equals(identifier2) == false)
			bindings_.get(identifier2).add(identifier1);
	}

	public void addFeatureBindings(TileFeatureBindings other) {
		bindings_.putAll(other.bindings_);
	}
}
