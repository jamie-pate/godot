/**************************************************************************/
/*  GodotApp.java                                                         */
/**************************************************************************/
/*                         This file is part of:                          */
/*                             GODOT ENGINE                               */
/*                        https://godotengine.org                         */
/**************************************************************************/
/* Copyright (c) 2014-present Godot Engine contributors (see AUTHORS.md). */
/* Copyright (c) 2007-2014 Juan Linietsky, Ariel Manzur.                  */
/*                                                                        */
/* Permission is hereby granted, free of charge, to any person obtaining  */
/* a copy of this software and associated documentation files (the        */
/* "Software"), to deal in the Software without restriction, including    */
/* without limitation the rights to use, copy, modify, merge, publish,    */
/* distribute, sublicense, and/or sell copies of the Software, and to     */
/* permit persons to whom the Software is furnished to do so, subject to  */
/* the following conditions:                                              */
/*                                                                        */
/* The above copyright notice and this permission notice shall be         */
/* included in all copies or substantial portions of the Software.        */
/*                                                                        */
/* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,        */
/* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF     */
/* MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. */
/* IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY   */
/* CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,   */
/* TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE      */
/* SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.                 */
/**************************************************************************/

package com.godot.game;

import org.godotengine.godot.Godot;
import org.godotengine.godot.GodotActivity;
import org.godotengine.godot.utils.ProcessPhoenix;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.splashscreen.SplashScreen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import com.godot.game.BuildConfig;

/**
 * Template activity for Godot Android builds.
 * Feel free to extend and modify this class for your custom logic.
 */
public class GodotApp extends GodotActivity {
	static {
		// .NET libraries.
		if (BuildConfig.FLAVOR.equals("mono")) {
			try {
				Log.v("GODOT", "Loading System.Security.Cryptography.Native.Android library");
				System.loadLibrary("System.Security.Cryptography.Native.Android");
			} catch (UnsatisfiedLinkError e) {
				Log.e("GODOT", "Unable to load System.Security.Cryptography.Native.Android library");
			}
		}
	}

	// Shouldn't conflict with BaseGodotEditor.RUN_GAME_INFO etc
	private final static int WINDOW_ID = 668;
	private final static String TAG = GodotApp.class.getSimpleName();

	private ArrayList<String> commandLineParams = new ArrayList<String>();

	@Override
	public int onNewGodotInstanceRequested(String[] args) {
		// Launch a new activity
		Log.d(TAG, "Restarting with parameters " + String.join(",", args));
		Godot godot = getGodot();
		Intent newInstance = new Intent()
									 .setComponent(new ComponentName(this, GodotApp.class))
									 .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
									 .putExtra(getEXTRA_COMMAND_LINE_PARAMS(), args);
		if (godot != null) {
			godot.destroyAndKillProcess(
					() -> ProcessPhoenix.triggerRebirth(this, newInstance));
		} else {
			ProcessPhoenix.triggerRebirth(this, newInstance);
		}
		return WINDOW_ID;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		String[] params = intent.getStringArrayExtra(getEXTRA_COMMAND_LINE_PARAMS());
		Log.d(TAG, "Starting intent " + intent + " with parameters " + (params != null ? String.join(" ", params) : "null"));
		updateCommandLineParams(params != null && params.length > 0 ? Arrays.asList(params) : Collections.emptyList());
		SplashScreen.installSplashScreen(this);
		super.onCreate(savedInstanceState);
	}
}
