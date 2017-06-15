package org.glud.arsimpleapp;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import org.glud.arsimpleapp.main;
import org.glud.trascendentar_android.ARLauncher;

public class AndroidLauncher extends ARLauncher {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new main(this), config);
	}

	@Override
	public void configureARScene() {
		loadMarker("hiroMarker",MarkerType.SINGLE,"Data/hiro.patt",8);
	}
}
