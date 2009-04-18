/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Mar 13, 2009
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
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.javassonne.networking.HostMonitor;
import org.javassonne.networking.impl.CachedHost;

// It would be nice to make this use reflection to figure out the available "get___" methods on RemoteHost, 
//    and naturally update the table using the available methods

/**
 * Keeps track of all hosts we know about, and keeps caches of their information
 * internally
 * 
 * Updates itself automatically
 */
public class ConnectedHosts extends TimerTask implements TableModel {

	private List<CachedHost> tableData_;
	private ArrayList<TableModelListener> observers_;

	public ConnectedHosts() {
		tableData_ = new ArrayList<CachedHost>();
		observers_ = new ArrayList<TableModelListener>();
		updateData();

		// Schedule ourselves to update
		Timer t = new Timer("AvailableNetworkHosts Model Updater");
		t.scheduleAtFixedRate(this, 0, 1000);
	}

	public void addTableModelListener(TableModelListener l) {
		observers_.add(l);
	}

	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	public int getColumnCount() {
		// Name, Desc, Status
		return 3;
	}

	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return "Name";
		case 1:
			return "Description";
		case 2:
		default:
			return "Status";
		}
	}

	public int getRowCount() {
		return tableData_.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		synchronized (tableData_) {
			switch (columnIndex) {
			case 0:
				return tableData_.get(rowIndex).getName();
			case 1:
				return tableData_.get(rowIndex).getURI();
			case 2:
			default:
				try {
					return tableData_.get(rowIndex).getStatus().text;
				} catch (Exception e) {
					return null;
				}
			}
		}

	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public void removeTableModelListener(TableModelListener l) {
		observers_.remove(l);
	}

	// Run ourselves as a TimerTask so we update the model occasionally
	public void run() {
		updateData();
	}

	public void setValueAt(Object value, int rowIndex, int columnIndex) {
	}

	private void updateData() {
		Boolean notify = false;
		synchronized (tableData_) {
			List<CachedHost> newTableData = HostMonitor.getHostsCopy();
			if (tableData_ != null){
				if (tableData_.size() != newTableData.size())
					notify = true;
				for (int ii = 0; ii < tableData_.size(); ii++)
					if (!newTableData.get(ii).equals(tableData_.get(ii)))
						notify = true;
			} else {
				notify = true;
			}
			tableData_ = newTableData;
		}
		if (notify) 
			notifyListeners();
	}

	private void notifyListeners() {
		TableModelEvent event = new TableModelEvent(this);
		for (Iterator<TableModelListener> it = observers_.iterator(); it
				.hasNext();)
			it.next().tableChanged(event);
	}
}
