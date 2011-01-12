/***********************************************************************
 *   MT4j Extension: MTCircularMenu
 *   
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License (LGPL)
 *   as published by the Free Software Foundation, either version 3
 *   of the License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the LGPL
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 ***********************************************************************/
package org.mt4jx.components.visibleComponents.widgets.circularmenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.mt4jx.util.animation.AnimationUtil;

/**
 * @author Uwe Laufs
 *
 */
public class DefaultScaleOutActionListener implements ActionListener {
	private MTCircularMenuSegment segment;
	public DefaultScaleOutActionListener(MTCircularMenuSegment segment){
		System.out.println("DefaultScaleOutActionListener created");
		this.segment = segment;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("DefaultScaleOutActionListener.actionPerformed called.");
		System.out.println("segment.hasChildren(): " + this.segment.hasChildren() );
		if(!this.segment.hasChildren()){
			AnimationUtil.scaleOut(this.segment.getParentMenu(), false);
		}
	}
	
}
