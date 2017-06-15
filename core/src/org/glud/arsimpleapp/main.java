package org.glud.arsimpleapp;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.glud.trascendentAR.ARCamera;
import org.glud.trascendentAR.ARToolKitManager;

import static com.badlogic.gdx.Gdx.gl;

public class main extends ApplicationAdapter {
	ARToolKitManager arManager; //Accede a los métodos de realidad aumentada
	AssetManager assetManager; //Se usa para cargar recursos
	ModelInstance koko; //La instancia del modelo que se va a usar
	ARCamera camera; //Cámara de realidad aumentada
	ModelBatch batch_3d; //Este objeto se encarga de pintar todos las instancias 3D en pantalla
	Environment environment; //Controla la iluminación del espacio
	Matrix4 transform = new Matrix4(); //Matriz auxiliar para manipular modelos si es necesesario
	Stage stage; //Dibuja todos los objetos 2D en pantalla y recibe entradas (Ej: Toque de un dedo)
	Button cameraPrefsButton;

	public main(ARToolKitManager arManager){
		this.arManager = arManager;
	}

	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		//Configurar cámara de libGDX
		camera = new ARCamera(67,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		camera.position.set(0f,0f,1f);
		camera.lookAt(0,0,0);
		camera.near = 0;
		camera.far = 1000f;
		camera.update();

		assetManager = new AssetManager();
		assetManager.load("koko.g3db", Model.class);
		assetManager.load("cam_button_down.png", Texture.class);
		assetManager.load("cam_button_up.png", Texture.class);
		assetManager.finishLoading();

		koko = new ModelInstance(assetManager.get("koko.g3db",Model.class));

		//Adding lights
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		batch_3d = new ModelBatch();

		stage = new Stage(new ScreenViewport());
		/* Create a button to open the camera preferences activity. First we define what images will be rendered when up and down
		 * Crear un botón para abrir la actividad de preferencias de camara. Primero definimos que imagenes mostrar cuando esta arriba y abajo
		 */
		Button.ButtonStyle buttonStyle = new Button.ButtonStyle();
		buttonStyle.up = new Image(assetManager.get("cam_button_up.png",Texture.class)).getDrawable();
		buttonStyle.down = new Image(assetManager.get("cam_button_down.png",Texture.class)).getDrawable();
		cameraPrefsButton = new Button(buttonStyle);
		//Damos una posicion en la parte superior derecha de la pantalla
		cameraPrefsButton.setPosition(stage.getWidth() - 20 - cameraPrefsButton.getHeight(),stage.getHeight() - 20 - cameraPrefsButton.getHeight());
		/* Recognize when button is clicked and open camera preferences using arToolKitMangaer
		 * Reconoce cuando el botón se ha presionado y abre preferencias de cámara
		 */
		cameraPrefsButton.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				arManager.openCameraPreferences();
			}
		});

		/* Let's add the button to the stage
		 * Añadimos el botón al stage
		 */
		stage.addActor(cameraPrefsButton);
		/*
		 * Finalmente como tenemos un boton que se puede presionar, debemos hacer que el stage reciba entradas
		 * Finally as we have a button to be pressed, we need to make stage to receive inputs
		 */
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render () {
		gl.glClearColor(0, 0, 0, 0);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		if(!arManager.arRunning())return;

		camera.projection.set(arManager.getProjectionMatrix());
		String markerID = "hiroMarker";
		if (arManager.markerVisible(markerID)) {
			transform.set(arManager.getTransformMatrix(markerID));
		/* Actualizar Cámara
		 * Update camera
		 */
			transform.getTranslation(camera.position);
			camera.position.scl(-1);
			camera.update();

		/* Dependiendo de las coordenadas del modelo puede necesitar rotarlo
		 * Depending from model coordinates it may be desired to apply a rotation
		 */
			transform.rotate(1, 0, 0, 90);
			koko.transform.set(transform);

			/*
			 * render 3D objects for this marker
			 * Pinte objetos 3D asociados a este marcador
			 */
			batch_3d.begin(camera);
			batch_3d.render(koko, environment);
			batch_3d.end();
		}

		/*
		 * Update and render 2D with inputs
		 * Actualizar y dibujar objetos 2D
		 */
		stage.act();
		stage.draw();
	}
	
	@Override
	public void dispose () {
		stage.dispose();
		batch_3d.dispose();
		assetManager.dispose();
	}
}
