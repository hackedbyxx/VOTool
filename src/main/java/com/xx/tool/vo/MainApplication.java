package com.xx.tool.vo;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.concurrent.Callable;

import com.xx.tool.vo.bean.DBConfig;
import com.xx.tool.vo.util.DBUtil;
import com.xx.tool.vo.util.SqlUtil;

import com.xx.tool.vo.util.XMLUtil;
import javafx.application.Application;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.xml.bind.JAXBException;

public class MainApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private static int method = 0;
    
    private static Alert alert;
    
    private ProgressIndicator progressIndicator;
    
    private TextArea input;

    private String title = "VO生成工具 v0.0.2 By XX";

    @Override
    public void start(Stage primaryStage) throws Exception {
    	BorderPane pane = new BorderPane();
    	pane.setPrefSize(1300, 800);
        //pane.getStyleClass().add("panel-primary");

        progressIndicator = new ProgressIndicator(0);
        progressIndicator.setVisible(false);
        progressIndicator.setMaxSize(150, 150);
        
        VBox centerPane = new VBox();
        centerPane.getChildren().add(progressIndicator);
    	pane.setCenter(centerPane);
        
        input = new TextArea();
        input.setMaxSize(600, 800);
        input.setPromptText("请输入不包含Schema的表名");
        input.setStyle("-fx-font-size:16px;");
        TextArea resultTextArea = new TextArea();
        resultTextArea.setMaxSize(600, 800);
        resultTextArea.setStyle("-fx-font-size:16px;");

        ToggleGroup group = new ToggleGroup();

        RadioButton rb1 = new RadioButton("单表生成VO");
        rb1.setSelected(true);
        rb1.setStyle("-fx-font-size:12px;");
        rb1.setToggleGroup(group);
        rb1.setUserData(0);

        RadioButton rb2 = new RadioButton("SQL语句生成VO");
        rb2.setStyle("-fx-font-size:12px;");
        rb2.setToggleGroup(group);
        rb2.setUserData(1);

        RadioButton rb3 = new RadioButton("SQL转StringBuilder");
        rb3.setStyle("-fx-font-size:12px;");
        rb3.setToggleGroup(group);
        rb3.setUserData(2);

        RadioButton rb4 = new RadioButton("StringBuilder转SQL");
        rb4.setStyle("-fx-font-size:12px;");
        rb4.setToggleGroup(group);
        rb4.setUserData(3);

        // 选中某个单选框时输出选中的值
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
                if (group.getSelectedToggle() != null) {
                    int data = (int) group.getSelectedToggle().getUserData();
                    
                    if (data == 0) {
                        method = 0;
                        input.setText("");
                        input.setPromptText("请输入不包含Schema的表名");
                    } else if (data == 1) {
                        method = 1;
						input.setText("");
                        input.setPromptText("请输入SQL");
                    } else if (data == 2) {
                        method = 2;
						input.setText("");
                        input.setPromptText("请输入SQL(不含中文)");
                    } else if (data == 3) {
                        method = 3;
						input.setText("");
                        input.setPromptText("StringBuilder");
                    }
                }
            }
        });
        
        Button button = new Button("开始生成");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	String errMsg = "";
                try {
                	ProgressTask task = new ProgressTask();
                	resultTextArea.textProperty().bindBidirectional((Property<String>) task.messageProperty());
                	//bind不能塞值 bindBidirectional 可以塞值
                	progressIndicator.visibleProperty().bindBidirectional((Property<Boolean>) task.runningProperty());
                    progressIndicator.progressProperty().bindBidirectional((Property<Number>) task.progressProperty());
                	new Thread(task).start();
                    
                } catch (Exception e) {
                    resultTextArea.setText("出现异常:" + errMsg + e);
                } finally {
                }
            }
        });
        button.prefWidthProperty().bind(pane.prefWidthProperty().add(10));
        button.setStyle("-fx-font-size:20px;");//-fx-color:#1E90FF;-fx-font-color:#000;

		Button configBtn = new Button("数据库配置");
		configBtn.setMinHeight(30);
		configBtn.setOnAction(e->{
			showDialog(primaryStage);
		});

        HBox hBox = new HBox();

        hBox.setAlignment(Pos.CENTER_LEFT);
		hBox.setSpacing(10);
        hBox.setPadding(new Insets(10,3,10,3));
        hBox.getChildren().add(rb1);
        hBox.getChildren().add(rb2);
        hBox.getChildren().add(rb3);
        hBox.getChildren().add(rb4);
		hBox.getChildren().add(configBtn);

        pane.setTop(hBox);
        pane.setLeft(input);
        BorderPane.setMargin(input, new Insets(0, 0, 5, 5));
        pane.setRight(resultTextArea);
        BorderPane.setMargin(resultTextArea, new Insets(0, 5, 5, 0));
        pane.setBottom(button);

        Scene scene = new Scene(pane, 1300, 800);
		scene.getStylesheets().add(this.getClass().getClassLoader().getResource("style.css").toExternalForm());
        // scene.getStylesheets().add("org/kordamp/bootstrapfx/bootstrapfx.css");
        // new JMetro(JMetro.Style.LIGHT).applyTheme(scene);
        primaryStage.setTitle(title);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        
        Image image= new Image(this.getClass().getClassLoader().getResourceAsStream("icon.png"));
        primaryStage.getIcons().add(image);
        primaryStage.show();
    }

	/**
	 * 打开数据库配置窗口
	 * @param primaryStage
	 */
	private void showDialog(Stage primaryStage) {
		final Stage dialog = new Stage();
		dialog.setResizable(false);
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.initOwner(primaryStage);

		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(20,40,20,40));
		gridPane.setHgap(10);
		gridPane.setVgap(12);

		Label jdbcClassLabel = new Label("Oracle驱动类");
		TextField jdbcClassTF = new TextField();
		jdbcClassTF.setMinWidth(320);
		gridPane.addRow(0,jdbcClassLabel,jdbcClassTF);

		Label jdbcUrlLabel = new Label("数据库链接");
		TextField jdbcUrlTF = new TextField();
		jdbcUrlTF.setPromptText("jdbc:oracle:thin:@");
		gridPane.addRow(1,jdbcUrlLabel,jdbcUrlTF);

		Label userLabel = new Label("用户");
		TextField userTF = new TextField();
		gridPane.addRow(2,userLabel,userTF);

		Label passwordLabel = new Label("密码");
		PasswordField passwordTF = new PasswordField();
		gridPane.addRow(3,passwordLabel,passwordTF);

		Button testBtn = new Button("测试数据库连接");
		Button saveBtn = new Button("保存");
		gridPane.addRow(4,saveBtn,testBtn);

		testBtn.setOnAction(e->{
			testConn();
		});

		saveBtn.setOnAction(e->{
			DBConfig dbConfig = new DBConfig();
			dbConfig.setJdbcClass(jdbcClassTF.getText());
			dbConfig.setUrl(jdbcUrlTF.getText());
			dbConfig.setUser(userTF.getText());
			dbConfig.setPassword(passwordTF.getText());
			try {
				XMLUtil.saveDBConfigToFile(dbConfig);
				alert = new Alert(AlertType.INFORMATION);
				alert.titleProperty().set("提示");
				alert.headerTextProperty().set("保存成功");
				alert.showAndWait();
			} catch (JAXBException ex) {
				ex.printStackTrace();
			}
		});

		try {
			DBConfig dbConfig = XMLUtil.loadDBConfigFromFile();
			jdbcClassTF.setText(dbConfig.getJdbcClass());
			jdbcUrlTF.setText(dbConfig.getUrl());
			userTF.setText(dbConfig.getUser());
			passwordTF.setText(dbConfig.getPassword());
		} catch (JAXBException ex) {
			ex.printStackTrace();
		}

		Scene dialogScene = new Scene(gridPane, 500, 300);
		dialog.setScene(dialogScene);
		Image image= new Image(this.getClass().getClassLoader().getResourceAsStream("icon.png"));
		dialog.getIcons().add(image);
		dialog.show();
	}

	/**
	 * 测试数据库连接
	 */
	public static void testConn() {
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				try {
					Connection conn = DBUtil.getJDBCConn();
					if(conn!=null) {
						conn.close();
					}
				} catch (Exception e) {
					updateMessage("数据库连接失败："+e);
					throw e;
				}
				return null;
			}

			@Override
			protected void succeeded() {
				// super.succeeded();
				// updateMessage("Succeeded");
				alert = new Alert(AlertType.INFORMATION);
				alert.titleProperty().set("提示");
				alert.headerTextProperty().set("连接成功！");
				alert.showAndWait();
			}

			@Override
			protected void cancelled() {
				super.cancelled();
				// progressIndicator.setVisible(false);
				updateMessage("Cancelled");
			}

			@Override
			protected void failed() {
				super.failed();
				//updateMessage("Failed");
				alert = new Alert(AlertType.WARNING);
				alert.titleProperty().set("提示");
				alert.headerTextProperty().set(getMessage());
				alert.showAndWait();
			}
			
		};
		new Thread(task).start();
	}
    
    /**
     *	 异步任务
     * @param isSimple 是否简单模式（不使用parameterTypes）
     * @param clazz 方法class
     * @param methodName 方法名
     * @param parameterTypes 参数class
     * @param params 参数
     * @return
     * @throws Exception
     */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object asyncWork(boolean isSimple, Class clazz, String methodName, Class[] parameterTypes,
			Object... params) throws Exception {
		Object call = new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				Object result = null;
				try {
					if (isSimple) {
						Method[] methods = clazz.getMethods();
						for (Method method : methods) {
							if (method.getName().equals(methodName)) {
								return method.invoke(clazz, params);
							}
						}
					} else {
						Method method = clazz.getMethod(methodName, parameterTypes);
						result = method.invoke(clazz, params);
					}
				} catch (InvocationTargetException e) {
					throw new Exception(e.getCause());
				}
				return result;
			}

		}.call();

		return call;
	}

	
	public class ProgressTask extends Task<Void> {
		
		@Override
		protected void succeeded() {
			// super.succeeded();
			// updateMessage("Succeeded");
		}

		@Override
		protected void cancelled() {
			super.cancelled();
			// progressIndicator.setVisible(false);
			updateMessage("Cancelled");
		}

		@Override
		protected void failed() {
			super.failed();
			//updateMessage("Failed");
		}

		@Override
		protected Void call() throws Exception {
			// progressIndicator.setProgress(-1);
			String errMsg = "";
			try {
				//Thread.sleep(500);
				updateMessage("加载中...");
				String result = "";
				if (method == 0) {
					result = SqlUtil.getColumnByTableName(input.getText());
					errMsg = "未查询到数据，请检查表及数据库配置是否正确";
					result = result.equals("") ? errMsg : result;
				} else if (method == 1) {
					result = SqlUtil.getSegmentsWithType(input.getText());
				} else if (method == 2) {
					result = SqlUtil.sqlTOStringBuilder(input.getText());
				} else if (method == 3) {
					result = SqlUtil.StringBuilderToString(input.getText());
				}
				
				updateProgress(100, 100);
				updateMessage(result);
			} catch (Exception e) {
				updateMessage("出现异常:" + errMsg + e);
			}

			return null;
		}
	}
}
