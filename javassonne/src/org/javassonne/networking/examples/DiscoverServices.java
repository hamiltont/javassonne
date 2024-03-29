/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Mar 4, 2009
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

package org.javassonne.networking.examples;

//Licensed under Apache License version 2.0
//Original license LGPL

//%Z%%M%, %I%, %G%
//
//This library is free software; you can redistribute it and/or
//modify it under the terms of the GNU Lesser General Public
//License as published by the Free Software Foundation; either
//version 2.1 of the License, or (at your option) any later version.
//
//This library is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//Lesser General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public
//License along with this library; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

import java.io.IOException;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

/**
* Sample Code for Service Discovery using JmDNS and a ServiceListener.
* <p>
* Run the main method of this class. It listens for HTTP services and lists
* all changes on System.out.
*
* @author  Werner Randelshofer
* @version 	%I%, %G%
*/
public class DiscoverServices {
  
  static class SampleListener implements ServiceListener {
      public void serviceAdded(ServiceEvent event) {
          System.out.println("Service added   : " + event.getName()+"."+event.getType());
      }
      public void serviceRemoved(ServiceEvent event) {
          System.out.println("Service removed : " + event.getName()+"."+event.getType());
      }
      public void serviceResolved(ServiceEvent event) {
          System.out.println("Service resolved: " + event.getInfo());
      }
  }
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
      try {
          JmDNS jmdns = JmDNS.create();
          jmdns.addServiceListener("_http._tcp.local.", new SampleListener());
          
          System.out.println("Press q and Enter, to quit");
          int b;
          while ((b = System.in.read()) != -1 && (char) b != 'q'); 
          jmdns.close();
          System.out.println("Done");
      } catch (IOException e) {
          e.printStackTrace();
      }
  }
}
