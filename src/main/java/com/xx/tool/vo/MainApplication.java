package com.xx.tool.vo;

import com.xx.tool.vo.util.SQLUtil;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApplication extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	private static int method = 0;

	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane pane = new BorderPane();
		// pane.getStyleClass().add("panel-primary");
		TextArea input = new TextArea();
		input.setMaxSize(600, 800);
		input.setText("�����벻����Schema�ı���");
		input.setStyle("-fx-font-size:16px;");
		TextArea resultTextArea = new TextArea();
		resultTextArea.setMaxSize(600, 800);
		resultTextArea.setStyle("-fx-font-size:16px;");

		ToggleGroup group = new ToggleGroup();

		RadioButton rb1 = new RadioButton("��������");
		rb1.setSelected(true);
		rb1.setStyle("-fx-font-size:12px;");
		rb1.setToggleGroup(group);
		rb1.setUserData(0);

		RadioButton rb2 = new RadioButton("SQL�������");
		rb2.setStyle("-fx-font-size:12px;");
		rb2.setToggleGroup(group);
		rb2.setUserData(1);

		// ѡ��ĳ����ѡ��ʱ���ѡ�е�ֵ
		group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
				if (group.getSelectedToggle() != null) {
					int data = (int) group.getSelectedToggle().getUserData();
					if(data==0) {
						method=0;
						input.setText("�����벻����Schema�ı���");
					}else if(data==1) {
						method=1;
						input.setText("������SQL");
					}
					System.out.println();
				}
			}
		});

		Button button = new Button("��ʼ����");
		// button.getStyleClass().setAll("btn","btn-primary");
		ProgressForm progress = new ProgressForm(primaryStage);
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					if(method==0) {
						String result = SQLUtil.getColumnByTableName(input.getText());
						result = result.equals("")?"δ��ѯ�����ݣ���������ݿ������Ƿ���ȷ":result;
						resultTextArea.setText(result);
					}else if(method==1) {
						resultTextArea.setText(SQLUtil.getSegmentsWithType(input.getText()));
					}
				} catch (Exception e) {
					resultTextArea.setText("��ʽ������:" + e.getClass());
				} finally {
				}
			}
		});
		button.setMinSize(1200, 50);
		button.setStyle("-fx-font-size:20px;");
		
		HBox hBox = new HBox();
		
		hBox.setPadding(new Insets(5));
		hBox.getChildren().add(rb1);
		hBox.getChildren().add(rb2);
		hBox.setSpacing(10);
		
		pane.setTop(hBox);
		pane.setLeft(input);
		pane.setRight(resultTextArea);
		pane.setBottom(button);

		Scene scene = new Scene(pane, 1200, 800);
		// scene.getStylesheets().add("org/kordamp/bootstrapfx/bootstrapfx.css");
		// new JMetro(JMetro.Style.LIGHT).applyTheme(scene);
		primaryStage.setTitle("VO���ɹ��� v0.0.1 By XX");
		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

}
