package games.rednblack.editor.view.ui.box;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowListener;
import com.puremvc.patterns.mediator.SimpleMediator;
import com.puremvc.patterns.observer.Notification;
import games.rednblack.editor.HyperLap2DFacade;
import games.rednblack.editor.live.LiveScreenAdapter;
import games.rednblack.editor.live.WorldSizeVO;
import games.rednblack.editor.proxy.ProjectManager;
import games.rednblack.editor.proxy.ResolutionManager;
import games.rednblack.editor.proxy.ResourceManager;
import games.rednblack.editor.splash.SplashScreenAdapter;
import games.rednblack.editor.view.stage.Sandbox;
import games.rednblack.h2d.common.MsgAPI;

public class UILivePreviewBoxMediator extends SimpleMediator<UILivePreviewBox> {
	private static final String TAG = UILivePreviewBoxMediator.class.getCanonicalName();
	public static final String NAME = TAG;

	private final ResolutionManager resolutionManager;
	private final ResourceManager resourceManager;
	private final ProjectManager projectManager;

	public UILivePreviewBoxMediator() {
		super(NAME, new UILivePreviewBox());
		facade = HyperLap2DFacade.getInstance();

		resolutionManager = facade.retrieveProxy(ResolutionManager.NAME);
		resourceManager = facade.retrieveProxy(ResourceManager.NAME);
		projectManager = facade.retrieveProxy(ProjectManager.NAME);
	}

	@Override
	public String[] listNotificationInterests() {
		return new String[]{
				ProjectManager.PROJECT_OPENED,
				MsgAPI.SCENE_LOADED,
				UILivePreviewBox.LIVE_PREVIEW_CLICKED
		};
	}

	@Override
	public void handleNotification(Notification notification) {
		super.handleNotification(notification);

		switch (notification.getName()) {
			case ProjectManager.PROJECT_OPENED:
				viewComponent.update();
				break;
			case MsgAPI.SCENE_LOADED:
				//TODO
				break;
			case UILivePreviewBox.LIVE_PREVIEW_CLICKED:
				showPreviewWindow();
				break;
			default:
				break;
		}
	}

	private Lwjgl3Window previewWindow;

	private void showPreviewWindow() {
		if (previewWindow == null) {
			createPreviewWindow();
		} else {
			previewWindow.closeWindow();
			createPreviewWindow();
		}
	}

	private void createPreviewWindow() {
		int previewWidth = resolutionManager.getOriginalResolution().width;
		int previewHeight = resolutionManager.getOriginalResolution().height;

		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(previewWidth, previewHeight);
		config.setTitle("HyperLap2D - Live Preview");
		config.setResizable(false);
		config.setIdleFPS(60);
		config.setWindowIcon("hyperlap_icon_96.png");

		Lwjgl3Application app = (Lwjgl3Application) Gdx.app;

		WorldSizeVO worldSizeVO = new WorldSizeVO(Sandbox.getInstance().getPixelPerWU(), previewWidth, previewHeight);
		LiveScreenAdapter liveScreenAdapter = new LiveScreenAdapter(worldSizeVO, resourceManager, projectManager.getCurrentSceneConfigVO().sceneName);

		previewWindow = app.newWindow(liveScreenAdapter, config);
		previewWindow.setWindowListener(new Lwjgl3WindowListener() {
			@Override
			public void created(Lwjgl3Window window) {

			}

			@Override
			public void iconified(boolean isIconified) {

			}

			@Override
			public void maximized(boolean isMaximized) {

			}

			@Override
			public void focusLost() {

			}

			@Override
			public void focusGained() {

			}

			@Override
			public boolean closeRequested() {
				previewWindow = null;
				return true;
			}

			@Override
			public void filesDropped(String[] files) {

			}

			@Override
			public void refreshRequested() {

			}
		});
	}
}
