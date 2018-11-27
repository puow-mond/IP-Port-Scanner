package application;

import java.net.URL;
import java.util.Collections;
import java.util.Comparator;

import com.sun.org.apache.bcel.internal.generic.NEW;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import util.NetworkScanner;

public class SampleController {
	@FXML
	private Button Play;
	@FXML
	private ListView<DisplayResult> view;
	@FXML
	private ListView<Integer> view2;
	@FXML
	private Button Stop;
	private Task<Void> scan_thread;
	private NetworkScanner scanner;
	@FXML
	private Button Help;
	@FXML
	private Button dnsBt;

	@FXML
	private ProgressBar bar;
	@FXML
	private TableView<DisplayResult> tableViewLeft;

	@FXML
	private TableView<DisplayResult> tableViewRight;

	@FXML
	public void initialize() {
		Play.setDisable(false);
		Stop.setDisable(true);
	}

	public void handleMouseClick(MouseEvent arg0) {
		System.out.println("clicked on " + view.getSelectionModel().getSelectedItem());
		System.out.println(view.getSelectionModel().getSelectedItem().getClass());
		ObservableList<Integer> list = FXCollections.observableArrayList();
		DisplayResult displayPort = view.getSelectionModel().getSelectedItem();
		if (displayPort != null) {
			view2.getItems().clear();
			for (Integer port : displayPort.getPort()) {
				list.add(port);
			}
			Collections.sort(list, portOrder);
			view2.setItems(list);
			view2.setCellFactory(new Callback<ListView<Integer>, ListCell<Integer>>() {
				@Override
				public ListCell<Integer> call(ListView<Integer> list) {
					return new UpdatePort();
				}
			});
		}

	}

	public void play(ActionEvent e) {
		Play.setDisable(true);
		Stop.setDisable(false);
		System.out.println("varit");
		scan_thread = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				scanner.scan();
				return null;
			}
		};
		bar.progressProperty().bind(scan_thread.progressProperty());
		new Thread(scan_thread).start();

	}

	public void setNetworkScanner(NetworkScanner scanner) {
		this.scanner = scanner;
	}

	@SuppressWarnings("unchecked")
	public void show(NetworkObserver obs) {
		if (obs.getList() != null) {
			// System.out.println(obs.getList());
			TableColumn<DisplayResult, String> ip = new TableColumn<>("IP Address");
			ip.setMinWidth(200);
			ip.setCellValueFactory(new PropertyValueFactory<DisplayResult, String>("ipaddr"));

			TableColumn<DisplayResult, String> ping = new TableColumn<>("Ping");
			ping.setMinWidth(200);
			ping.setCellValueFactory(new PropertyValueFactory<DisplayResult, String>("ping"));

			tableViewLeft.setItems(obs.getList());
			tableViewLeft.getColumns().addAll(ip, ping);
		}

	}

	public void stop(ActionEvent e) {
		Play.setDisable(false);
		Stop.setDisable(true);
		scanner.stop();
		System.out.println("Stop");
	}

	TableView<WellKnownPorts> tViewWellKnownPorts;

	public ObservableList<WellKnownPorts> getWellPort() {
		ObservableList<WellKnownPorts> wPorts = FXCollections.observableArrayList();
		wPorts.add(new WellKnownPorts("HTTP", "80"));
		wPorts.add(new WellKnownPorts("HTTPS", "443"));
		wPorts.add(new WellKnownPorts("FTP", "20,21"));
		wPorts.add(new WellKnownPorts("DNS", "53"));
		wPorts.add(new WellKnownPorts("SMTP", "25"));
		wPorts.add(new WellKnownPorts("POP3", "110"));
		wPorts.add(new WellKnownPorts("IMAP", "143"));
		wPorts.add(new WellKnownPorts("Telnet", "23"));
		wPorts.add(new WellKnownPorts("SSH", "22"));
		return wPorts;
	}

	private Stage priStage = new Stage();

	public void help(ActionEvent e) {
		priStage.setTitle("Help Information");

		TableColumn<WellKnownPorts, String> portName = new TableColumn<>("Port");
		portName.setMinWidth(200);
		portName.setCellValueFactory(new PropertyValueFactory<>("port"));

		TableColumn<WellKnownPorts, String> serviceName = new TableColumn<>("Service");
		serviceName.setMinWidth(200);
		serviceName.setCellValueFactory(new PropertyValueFactory<>("service"));
		tViewWellKnownPorts = new TableView<>();
		tViewWellKnownPorts.setItems(getWellPort());
		tViewWellKnownPorts.getColumns().addAll(serviceName, portName);

		VBox vBox = new VBox();
		vBox.getChildren().addAll(tViewWellKnownPorts);
		Scene scene = new Scene(vBox, 400, 270);
		priStage.setScene(scene);
		priStage.show();
	}

	Button convertBt = new Button();

	Stage stageConverter;

	public void dns(ActionEvent e) {
		try {
			stageConverter = new Stage();
			URL url = getClass().getResource("Converter.fxml");
			if (url == null) {
				System.out.println("Couldn't find file: Converter.fxml");
				Platform.exit();
			}
			FXMLLoader loader = new FXMLLoader(url);
			Parent root = loader.load();

			Scene scene = new Scene(root);
			stageConverter.setScene(scene);
			stageConverter.sizeToScene();
			stageConverter.setTitle("Convert DNS");
			stageConverter.show();
		} catch (Exception exception) {
			exception.printStackTrace();
			return;
		}
	}

	private static class Update extends ListCell<DisplayResult> {
		@Override
		public void updateItem(DisplayResult item, boolean empty) {
			super.updateItem(item, empty);
			if (item != null) {
				setText(item.getIpaddr());
			}
		}
	}

	private static class UpdatePort extends ListCell<Integer> {
		@Override
		public void updateItem(Integer item, boolean empty) {
			super.updateItem(item, empty);
			if (item != null) {
				setText(item + "");
			}
		}
	}

	Comparator<Integer> portOrder = new Comparator<Integer>() {
		@Override
		public int compare(Integer m1, Integer m2) {
			return m1.compareTo(m2);
		}
	};

}
