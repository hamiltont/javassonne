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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.javassonne.networking.HostMonitor;
import org.javassonne.networking.RemoteHost;

// It would be nice to make this use reflection to figure out the available "get___" methods on RemoteHost, 
//    and naturally update the table using the available methods

public class AvailableNetworkHosts extends TimerTask implements TableModel {

	private ArrayList<RemoteHost> tableData_;
	private ArrayList<String> hostURIs_;				// Allows us to see if we already know about
														//   a host without having to send a redundant 
														//   network query
	private ArrayList<TableModelListener> observers_;

	public AvailableNetworkHosts() {
		tableData_ = new ArrayList<RemoteHost>();
		observers_ = new ArrayList<TableModelListener>();
		hostURIs_ = new ArrayList<String>();
		updateData();

		// Schedule ourselves to update
		Timer t = new Timer("NetworkHosts UpdateData Timer");
		t.scheduleAtFixedRate(this, 0, 10000);
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
		switch (columnIndex) {
		case 0:
			return tableData_.get(rowIndex).getName();
		case 1:
			return tableData_.get(rowIndex).getURI();
		case 2:
		default:
			return tableData_.get(rowIndex).getStatus();
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
		try {
			updateData();
		} catch (Exception e) {
			System.out.println("gotcha");
		}
	}

	public void setValueAt(Object value, int rowIndex, int columnIndex) {
	}

	// TODO check that the call to contains actually works
	private void updateData() {
		List<RemoteHost> hosts = HostMonitor.getInstance().getHosts();

		boolean modelChanged = false;
		for (Iterator<RemoteHost> it = hosts.iterator(); it.hasNext();) {
			RemoteHost next = it.next();
			String nextURI = next.getURI();
			if (hostURIs_.contains(nextURI) == false) {
				tableData_.add(next);
				hostURIs_.add(nextURI);
				modelChanged = true;
			}
		}

		// If the model changed, let all observers know
		if (modelChanged) {
			for (Iterator<TableModelListener> it = observers_.iterator(); it
					.hasNext();) {
				TableModelListener next = it.next();
				TableModelEvent e = new TableModelEvent(this);
				next.tableChanged(e);
			}
		}
	}
}
