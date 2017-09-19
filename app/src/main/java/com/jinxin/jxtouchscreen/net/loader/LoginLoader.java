package com.jinxin.jxtouchscreen.net.loader;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.model.CloudSetting;
import com.jinxin.jxtouchscreen.model.Customer;
import com.jinxin.jxtouchscreen.model.CustomerArea;
import com.jinxin.jxtouchscreen.model.CustomerDataSync;
import com.jinxin.jxtouchscreen.model.CustomerPattern;
import com.jinxin.jxtouchscreen.model.CustomerProduct;
import com.jinxin.jxtouchscreen.model.FunDetail;
import com.jinxin.jxtouchscreen.model.FunDetailConfig;
import com.jinxin.jxtouchscreen.model.ProductFun;
import com.jinxin.jxtouchscreen.model.ProductPatternOperation;
import com.jinxin.jxtouchscreen.model.ProductRegister;
import com.jinxin.jxtouchscreen.model.User;
import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.jinxin.jxtouchscreen.db.DBM;
import com.jinxin.jxtouchscreen.db.SPM;
import com.jinxin.jxtouchscreen.event.LoginSuccessEvent;
import com.jinxin.jxtouchscreen.model.WakeWord;
import com.jinxin.jxtouchscreen.net.HttpManager;
import com.jinxin.jxtouchscreen.net.base.Callback;
import com.jinxin.jxtouchscreen.net.request.CloudSettingRequest;
import com.jinxin.jxtouchscreen.net.request.CustomerAreaRequest;
import com.jinxin.jxtouchscreen.net.request.CustomerDataSyncRequest;
import com.jinxin.jxtouchscreen.net.request.CustomerPatternRequest;
import com.jinxin.jxtouchscreen.net.request.CustomerProductRequest;
import com.jinxin.jxtouchscreen.net.request.CustomerRequest;
import com.jinxin.jxtouchscreen.net.request.FunDetailConfigRequest;
import com.jinxin.jxtouchscreen.net.request.FunDetailRequest;
import com.jinxin.jxtouchscreen.net.request.LoginRequest;
import com.jinxin.jxtouchscreen.net.request.ProductFunRequest;
import com.jinxin.jxtouchscreen.net.request.ProductPatternOperationRequest;
import com.jinxin.jxtouchscreen.net.request.ProductRegisterRequest;
import com.jinxin.jxtouchscreen.net.request.WakeWordRequest;
import com.jinxin.jxtouchscreen.util.FileUtil;
import com.jinxin.jxtouchscreen.util.L;
import com.jinxin.jxtouchscreen.util.StringUtils;
import com.jinxin.jxtouchscreen.util.ToastUtil;
import com.litesuits.orm.db.assit.QueryBuilder;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2016/12/23.
 * 登录器
 */
public class LoginLoader {
	private static int taskAlreadyCount = 0;

	/**
	 * 计算已运行成功的任务数
	 */
	public static void count() {
		taskAlreadyCount++;
		L.w("-------------" + taskAlreadyCount + "-------------");
		if (taskAlreadyCount == 10) {
			//回到登录界面
			EventBus.getDefault().post(new LoginSuccessEvent());
			taskAlreadyCount = 0;
		}
	}

	public static void load(String account, String password) {

		L.w("当前线程：" + Thread.currentThread().getId() + "-->" + Thread.currentThread().getName());

		/* -------------- 登录请求 -------------- */
		HttpManager.getInstance().addRequest(new LoginRequest(new Callback<User>() {
			@Override
			public void onReceive(User user) {
				//存放用户常量
				SPM.saveStr(SPM.CONSTANT, LocalConstant.KEY_ACCOUNT, user.getAccount());
				SPM.saveStr(SPM.CONSTANT, LocalConstant.KEY_SUB_ACCOUNT, user.getSubAccunt());
				SPM.saveStr(SPM.CONSTANT, LocalConstant.KEY_PASSWORD, user.getSecretKey());

				//存入数据库，首先查重
				if (DBM.getCurrentOrm().query(new QueryBuilder<>(User.class).where("account = ?", new String[]{user.getAccount()})).contains(user))
					DBM.getCurrentOrm().update(user);
				else
					DBM.getCurrentOrm().save(user);

				/* -------------- 请求产品列表 -------------- */
				HttpManager.getInstance().addRequest(new ProductFunRequest(new Callback<ProductFun>() {
					@Override
					public void onReceive(List<ProductFun> t) {
						//存入数据库
						if (t.size() > 0) {
							DBM.getCurrentOrm().save(t);
						}
						count();
					}

					@Override
					public void onError(String error) {
						ToastUtil.showShort(BaseApp.getContext(), R.string.load_failed);
					}
				}));

				/* -------------- 请求设备列表 -------------- */
				HttpManager.getInstance().addRequest(new FunDetailRequest(new Callback<FunDetail>() {
					@Override
					public void onReceive(List<FunDetail> t) {
						//存入数据库
						if (t.size() > 0) {
							DBM.getCurrentOrm().save(t);
						}
						count();
					}

					@Override
					public void onError(String error) {
						ToastUtil.showShort(BaseApp.getContext(), R.string.load_failed);
					}
				}));

				/* -------------- 请求设备配置列表 -------------- */
				HttpManager.getInstance().addRequest(new FunDetailConfigRequest(new Callback<FunDetailConfig>() {
					@Override
					public void onReceive(List<FunDetailConfig> t) {
						//存入数据库
						if (t.size() > 0) {
							DBM.getCurrentOrm().save(t);
							Log.d("TAG", "请求的设备配置列表："+ t );
						}
						count();
					}

					@Override
					public void onError(String error) {
						ToastUtil.showShort(BaseApp.getContext(), R.string.load_failed);
					}
				}));

					/* -------------- 请求唤醒词列表 -------------- */
				HttpManager.getInstance().addRequest(new WakeWordRequest(new Callback<WakeWord>() {
					@Override
					public void onReceive(List<WakeWord> t) {
						//存入数据库
						if (t.size() > 0) {
							DBM.getCurrentOrm().save(t);
							Log.d("TAG", "拥有的唤醒词："+ t );
						}
						count();
					}

					@Override
					public void onError(String error) {
						ToastUtil.showShort(BaseApp.getContext(), R.string.load_failed);
					}
				}));

				/* -------------- 请求云端设置 -------------- */
				HttpManager.getInstance().addRequest(new CloudSettingRequest(new Callback<CloudSetting>() {
					@Override
					public void onReceive(List<CloudSetting> t) {
						//存入数据库
						if (t.size() > 0) {
							DBM.getCurrentOrm().save(t);
						}
						count();
					}

					@Override
					public void onError(String error) {
						ToastUtil.showShort(BaseApp.getContext(), R.string.load_failed);
					}
				}));

				/* -------------- 请求模式列表 -------------- */
				HttpManager.getInstance().addRequest(new CustomerPatternRequest(new Callback<CustomerPattern>() {
					@Override
					public void onReceive(List<CustomerPattern> t) {
						//查重，存入数据库
						int size = t.size();
						List<CustomerPattern> customerPatterns = DBM.getCurrentOrm().query(CustomerPattern.class);
						if (customerPatterns.size() > 0) {
							for (int i = 0; i < size; i++) {
								if (DBM.getCurrentOrm().query(new QueryBuilder<>(CustomerPattern.class).where("patternId = ?", new String[]{String.valueOf(t.get(i).getPatternId())})).contains(t.get(i))) {
									DBM.getCurrentOrm().update(t.get(i));
								} else {
									DBM.getCurrentOrm().save(t.get(i));
								}
							}
						} else {
							//如果是首次存入，则全部存入
							if (size > 0) {
								DBM.getCurrentOrm().save(t);
							}
						}

						//生成模式参数请求参数
						customerPatterns = DBM.getCurrentOrm().query(CustomerPattern.class);
						size = customerPatterns.size();

						StringBuilder sb = new StringBuilder();
						if(size>0){
							for (int i = 0; i < size - 1; i++) {
								sb.append(customerPatterns.get(i).getPatternId()).append(",");
							}
							sb.append(customerPatterns.get(size - 1).getPatternId());
						}


						/* -------------- 请求模式参数列表 -------------- */
						HttpManager.getInstance().addRequest(new ProductPatternOperationRequest(new Callback<ProductPatternOperation>() {
							@Override
							public void onReceive(List<ProductPatternOperation> t) {
								//查重，遍历各模式，填充模式参数
								int size = t.size();
								for (int i = 0; i < size; i++) {
									if (DBM.getCurrentOrm().query(new QueryBuilder<>(ProductPatternOperation.class).where("patternId = ?", new String[]{String.valueOf(t.get(i).getPatternId())})).contains(t.get(i))) {
										DBM.getCurrentOrm().update(t.get(i));
									} else {
										DBM.getCurrentOrm().save(t.get(i));
									}
								}
							}

							@Override
							public void onError(String error) {
								ToastUtil.showShort(BaseApp.getContext(), R.string.load_failed);
							}
						}, sb.toString()));
						count();
					}

					@Override
					public void onError(String error) {
						ToastUtil.showShort(BaseApp.getContext(), R.string.load_failed);
					}
				}));

				/* -------------- 请求已注册产品列表 -------------- */
				HttpManager.getInstance().addRequest(new ProductRegisterRequest(new Callback<ProductRegister>() {
					@Override
					public void onReceive(List<ProductRegister> t) {
						//存入数据库
						if (t.size() > 0) {
							DBM.getCurrentOrm().save(t);
						}
						count();
					}

					@Override
					public void onError(String error) {
						ToastUtil.showShort(BaseApp.getContext(), R.string.load_failed);
					}
				}));

				/* -------------- 请求用户列表 -------------- */
				HttpManager.getInstance().addRequest(new CustomerRequest(new Callback<Customer>() {
					@Override
					public void onReceive(Customer customer) {
						//查重，存入数据库
						if (DBM.getCurrentOrm().query(new QueryBuilder<>(Customer.class).where("customerId = ?", new String[]{customer.getCustomerId()})).contains(customer))
							DBM.getCurrentOrm().update(customer);
						else
							DBM.getCurrentOrm().save(customer);
						count();
					}

					@Override
					public void onError(String error) {
						ToastUtil.showShort(BaseApp.getContext(), R.string.load_failed);
					}
				}

				));

				/* -------------- 请求用户产品列表 -------------- */
				HttpManager.getInstance().addRequest(new CustomerProductRequest(new Callback<CustomerProduct>() {
					@Override
					public void onReceive(List<CustomerProduct> t) {
						//存入数据库
						if (t.size() > 0) {
							DBM.getCurrentOrm().save(t);
						}
						count();
					}

					@Override
					public void onError(String error) {
						ToastUtil.showShort(BaseApp.getContext(), R.string.load_failed);
					}
				}

				));

				/* -------------- 请求用户模式区域列表 -------------- */
				HttpManager.getInstance().addRequest(new CustomerAreaRequest(new Callback<CustomerArea>() {
					@Override
					public void onReceive(List<CustomerArea> t) {
						//存入数据库
						if (t.size() > 0) {
							DBM.getCurrentOrm().save(t);
						}
						count();
					}

					@Override
					public void onError(String error) {
						ToastUtil.showShort(BaseApp.getContext(), R.string.load_failed);
					}
				}

				));

				/* -------------- 请求用户数据同步列表 -------------- */
				HttpManager.getInstance().addRequest(new CustomerDataSyncRequest(new Callback<CustomerDataSync>() {
					@Override
					public void onReceive(List<CustomerDataSync> t) {
						//根据内容删除相应表内容
						SQLiteDatabase db = DBM.getCurrentOrm().getWritableDatabase();
						for (CustomerDataSync cds : t) {
							String tableName = cds.getModel();
							String whereClause = StringUtils.parseWhereClause(cds.getActionFieldName());
							String[] whereArgs = StringUtils.parseWhereArgs(cds.getActionId());
							try {
								if (DBM.getCurrentOrm().getTableManager().isSQLTableCreated(tableName))
									db.delete(tableName, whereClause, whereArgs);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						count();
					}

					@Override
					public void onError(String error) {
						ToastUtil.showShort(BaseApp.getContext(), R.string.load_failed);
					}
				}

				));

			}

			@Override
			public void onError(String error) {
				ToastUtil.showShort(BaseApp.getContext(), R.string.login_failed);
			}
		}, account, password));
	}

	public static void loadLocal(String account){
		copyGatewayDB();
	}

	private static void copyGatewayDB() {
		// 第一次运行应用程序时，加载数据库到data/data/当前包的名称/database/<db_name>
		File dir = new File("data/data/" + BaseApp.getContext().getPackageName() + "/databases");
		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdir();
		}
		if (BaseApp.getFriendContext() != null) {
			File gatewayDatabasesFile = BaseApp.getFriendContext().getDatabasePath("gateway.db");
			File gatewayDatabasesFileJournal = BaseApp.getFriendContext().getDatabasePath("gateway.db-journal");
			if (gatewayDatabasesFile == null) {
				return;
			}
			File file = new File(dir, "gateway.db");
			if (file.exists()) {
				file.delete();
			}
			File fileJournal = new File(dir, "gateway.db-journal");
			if (fileJournal.exists()) {
				fileJournal.delete();
			}
			try {
				FileUtil.copyFile(gatewayDatabasesFile, file);
				FileUtil.copyFile(gatewayDatabasesFileJournal, fileJournal);
			} catch (IOException e) {
				e.printStackTrace();
			}
			L.d("gateway.db", "DataBase Load Successfully");
			L.d("gateway.db-journal", "DataBase Load Successfully");
		}

	}
}
