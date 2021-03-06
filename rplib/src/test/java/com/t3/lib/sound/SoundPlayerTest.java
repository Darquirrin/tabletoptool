/*
 * Copyright (c) 2014 tabletoptool.com team.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     rptools.com team - initial implementation
 *     tabletoptool.com team - further development
 */
package com.t3.lib.sound;

import org.testng.annotations.Test;
import com.t3.sound.SoundPlayer;

public class SoundPlayerTest {

	@Test
	public void testPlay() throws Exception {
		SoundPlayer.play(SoundPlayerTest.class.getResource("door.mp3"));
		SoundPlayer.waitFor();
	}
}
