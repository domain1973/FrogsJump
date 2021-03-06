package com.ads.jump.frogs.android;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.ads.jump.frogs.Answer;
import com.ads.jump.frogs.Assets;
import com.ads.jump.frogs.PEvent;
import com.ads.jump.frogs.Series;
import com.ads.jump.frogs.Settings;
import com.ads.jump.frogs.screen.GameScreen;
import com.ads.jump.frogs.screen.MainScreen;
import com.baidu.inf.iis.bcs.BaiduBCS;
import com.baidu.inf.iis.bcs.auth.BCSCredentials;
import com.baidu.inf.iis.bcs.request.GetObjectRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OneKeyShareCallback;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by Administrator on 2014/10/2.
 */
public class PEventImpl extends PEvent {
    private AndroidLauncher launcher;
    private ProgressDialog progressDialog;
    private Handler handler;
    private String gameLogoImage;
    private String yybUrl;
    private boolean ads = false;

    public PEventImpl(AndroidLauncher androidLauncher) {
        launcher = androidLauncher;
        handler = new Handler();
        fillAd();
    }

    private void openNetworkFailDlg() {
        handler.post(new Runnable() {
            public void run() {
                new AlertDialog.Builder(launcher).setTitle(Constant.title).setMessage("连接不到网络,请检查哦!").
                        setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        }).setIcon(R.drawable.xiaozi).create().show();
            }
        });
    }

    @Override
    public void exit(final MainScreen ms) {
        handler.post(new Runnable() {
            private void ok() {
                save();
                int currentVersion = android.os.Build.VERSION.SDK_INT;
                if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    launcher.startActivity(startMain);
                    System.exit(0);
                } else {// android2.1
                    ActivityManager am = (ActivityManager) launcher.getSystemService(launcher.ACTIVITY_SERVICE);
                    am.restartPackage(launcher.getPackageName());
                }
            }

            public void run() {
                if (ads) {
                    new AlertDialog.Builder(launcher).setTitle(Constant.title).setMessage("确定要退出游戏吗?")
                            .setNeutralButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ok();
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ms.setTouchBack(true);
                        }
                    }).setPositiveButton("爱迪出品", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Uri uri = Uri.parse(Constant.adsUrl);
                            Intent it = new Intent(Intent.ACTION_VIEW, uri);
                            launcher.startActivity(it);
                        }
                    }).setIcon(R.drawable.xiaozi).create().show();
                } else {
                    new AlertDialog.Builder(launcher).setTitle(Constant.title).setMessage("确定要退出游戏吗?")
                            .setNeutralButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ok();
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ms.setTouchBack(true);
                        }
                    }).setIcon(R.drawable.xiaozi).create().show();
                }
            }
        });
    }

    @Override
    public void save() {
        SharedPreferences.Editor sharedata = launcher.getSharedPreferences("data", 0).edit();
        sharedata.putBoolean("music", Settings.musicEnabled);
        sharedata.putBoolean("sound", Settings.soundEnabled);
        sharedata.putInt("passNum", Settings.unlockGateNum);
        sharedata.putInt("helpNum", Settings.helpNum);
        StringBuffer sb = new StringBuffer();
        for (Integer starNum : Answer.gateStars) {
            sb.append(starNum).append(",");
        }
        sharedata.putString("starNum", sb.substring(0, sb.length() - 1));
        sharedata.putBoolean("adManager", Settings.adManager);
        sharedata.commit();
    }

    @Override
    public void about() {
        handler.post(new Runnable() {
            public void run() {
                new AlertDialog.Builder(launcher).setTitle(Constant.SHARE_TITLE).setMessage(
                        "版本: 1.0.0\n" +
                                "爱迪工作室 \n" +
                                "版权所有c 2015\n" +
                                "客户邮箱\n" +
                                "domainxu@foxmail.com\n" +
                                "工作室网址\n" +
                                "http://ads360.duapp.com/House"
                )
                        .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).setIcon(R.drawable.xiaozi).create().show();
            }
        });
    }

    @Override
    public void netSlowInfo() {
        handler.post(new Runnable() {
            public void run() {
                new AlertDialog.Builder(launcher).setTitle(Constant.SHARE_TITLE).setMessage(
                        "网速太慢,请稍候再试.")
                        .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).setIcon(R.drawable.xiaozi).create().show();
            }
        });
    }

    @Override
    public void spotAd() {
        handler.post(new Runnable() {
            public void run() {
                launcher.spot();
            }
        });
    }

    @Override
    public void sos(final GameScreen gs) {
        handler.post(new Runnable() {
            public void run() {
                new AlertDialog.Builder(launcher).setTitle(Constant.title).setMessage("您还有" + Settings.helpNum + "次机会,需要帮助吗?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Settings.helpNum = Settings.helpNum - 1;
                                gs.useSos();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setIcon(R.drawable.xiaozi).create().show();
            }
        });
    }

    @Override
    public void invalidateSos() {
        handler.post(new Runnable() {
            public void run() {
                new AlertDialog.Builder(launcher).setTitle(Constant.title).setMessage("智慧星不够,点击分享可以获取智慧星哦!")
                        .setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).setNeutralButton("分享", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showShare();
                    }
                }).setIcon(R.drawable.xiaozi).create().show();
            }
        });
    }

    @Override
    public void resetGame() {
        handler.post(new Runnable() {
            public void run() {
                new AlertDialog.Builder(launcher).setTitle(Constant.title).setMessage("是否重置您的进度?").setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Settings.unlockGateNum = 0;
                        Answer.gateStars.clear();
                        Answer.gateStars.add(0);
                        save();
                    }
                }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setIcon(R.drawable.xiaozi).create().show();
            }
        });
    }

    @Override
    public void share() {
        showShare();
    }

    @Override
    public void install(final Series series) {
        handler.post(new Runnable() {
            public void run() {
                new AlertDialog.Builder(launcher).setTitle(series.getName()).setMessage(series.getDetail())
                        .setPositiveButton("下载", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                openProgressBar();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        openFile(downLoadFile(series.getUrl()));
                                    }
                                }).start();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).setIcon(R.drawable.xiaozi).create().show();
            }
        });
    }

    @Override
    public boolean isNetworkEnable() {
        if (!isConnect2Net(launcher.getContext())) {
            openNetworkFailDlg();
            return false;
        }
        return true;
    }

    private void fillAd() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                initBucket();
            }
        }).start();
    }

    private boolean isConnect2Net(Context context) {
        try {
            ConnectivityManager con = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkinfo = con.getActiveNetworkInfo();
            if (networkinfo == null || !networkinfo.isAvailable()) {
                // 当前网络不可用
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void showShare() {
        if (!isConnect2Net(launcher.getContext())) {
            openNetworkFailDlg();
        } else {
            initImagePath();
            handler.post(new Runnable() {
                public void run() {
                    ShareSDK.initSDK(launcher);
                    OnekeyShare oks = new OnekeyShare();
                    oks.setNotification(R.drawable.ic_launcher, launcher.getContext().getString(R.string.app_name));
                    oks.setTitle(Constant.SHARE_TITLE);
                    oks.setText(Constant.SHARE_TEXT);
                    oks.setImagePath(gameLogoImage);
                    if (yybUrl != null && yybUrl.length() > 0) {
                        oks.setUrl(yybUrl);
                    } else {
                        oks.setUrl(Constant.gameUrl);
                    }
                    // 令编辑页面显示为Dialog模式
                    oks.setDialogMode();
                    // 在自动授权时可以禁用SSO方式
                    oks.disableSSOWhenAuthorize();
                    // 去除注释，则快捷分享的操作结果将通过OneKeyShareCallback回调
                    oks.setCallback(new OneKeyShareCallback());
                    oks.show(launcher.getContext());
                }
            });
        }
    }

    private void initImagePath() {
        try {
            String cachePath = cn.sharesdk.framework.utils.R.getCachePath(launcher, null);
            gameLogoImage = cachePath + "gamelogo.png";
            File file = new File(gameLogoImage);
            if (!file.exists()) {
                file.createNewFile();
                Bitmap pic = BitmapFactory.decodeResource(launcher.getResources(), R.drawable.ic_launcher);
                FileOutputStream fos = new FileOutputStream(file);
                pic.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            gameLogoImage = null;
        }
    }

    private File downLoadFile(final String httpUrl) {
        final String fileName = "ads.apk";
        String path1 = "/mnt/sdcard/update";
        File tmpFile = new File(path1);
        if (!tmpFile.exists()) {
            tmpFile.mkdir();
        }
        final File file = new File(path1 + "/" + fileName);
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream is = conn.getInputStream();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buf = new byte[256];
            conn.connect();
            double count = 0;
            if (conn.getResponseCode() >= 400) {
                Toast.makeText(launcher, "连接超时", Toast.LENGTH_SHORT).show();
            } else {
                while (count <= 100) {
                    if (is != null) {
                        int numRead = is.read(buf);
                        if (numRead <= 0) {
                            break;
                        } else {
                            fos.write(buf, 0, numRead);
                        }
                    } else {
                        break;
                    }
                }
            }
            conn.disconnect();
            fos.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        progressDialog.dismiss();
        return file;
    }

    private void openProgressBar() {
        progressDialog = new ProgressDialog(launcher.getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在下载,请稍候.");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
    }

    //打开APK程序代码
    private void openFile(File file) {
        Log.e("OpenFile", file.getName());
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        launcher.startActivity(intent);
    }

    private void initBucket() {
        try {
            File file = new File(Constant.path);
            if (!file.exists()) {
                file.mkdir();
            }
            BCSCredentials credentials = new BCSCredentials(Constant.accessKey, Constant.secretKey);
            BaiduBCS baiduBCS = new BaiduBCS(credentials, Constant.host);
            baiduBCS.setDefaultEncoding("UTF-8"); // Default UTF-8
            getObjectWithDestFile(baiduBCS, Constant.adAtlasStr, Constant.adAtlas);//系列图片信息
            getObjectWithDestFile(baiduBCS, Constant.urlStr, Constant.url);//下载地址
            getObjectWithDestFile(baiduBCS, Constant.yybStr, Constant.yyb);
            getObjectWithDestFile(baiduBCS, Constant.adsStr, Constant.ads);
            getObjectWithDestFile(baiduBCS, Constant.adPngStr, Constant.adPng);

            FileInputStream fis = new FileInputStream(Constant.yyb);
            byte[] bts = new byte[128];
            fis.read(bts);
            yybUrl = new String(bts).trim();
            //爱迪精品开关
            fis = new FileInputStream(Constant.ads);
            bts = new byte[128];
            fis.read(bts);
            ads = Boolean.parseBoolean(new String(bts).trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getObjectWithDestFile(BaiduBCS baiduBCS, String name, File file) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(Constant.bucket, name);
        baiduBCS.getObject(getObjectRequest, file);
    }

    @Override
    public boolean isAdEnable() {
        return ads;
    }

    @Override
    public void help(final int level) {
        handler.post(new Runnable() {
            public void run() {
                String readme = "";
                switch (level) {
                    case 0:
                        readme = "初级: \n" +
                                "1 飞机的摆放位置及方向都已经预先给出. \n" +
                                "2 正确地摆放拼图,让拼图上的飞机能覆盖白色飞机图案上.";
                        break;
                    case 1:
                        readme = "中级: \n" +
                                "1 飞机的摆放位置及飞行路线都已预先给出,但不包含飞机的摆放方向. \n" +
                                "2 将飞机分别放在圆圈标明的6个区点上.飞机的摆放方向必须顺着航线方向(虚线必须穿过机头和机尾).";
                        break;
                    case 2:
                        readme = "高级: \n" +
                                "1 飞机的航线及摆放方向都已预先给出,但不包含飞机的摆放位置. \n" +
                                "2 将拼图上的飞机对照箭头的方向放置在航线上.飞机的头尾方向必须与航线一致(从机头到机尾把飞机路线完整覆盖住).";
                        break;
                    case 3:
                        readme = "专家: \n" +
                                "1 仅仅提供跑道部分. \n" +
                                "2 将飞机放在直线跑道上(在同一条跑道上的飞机的头尾摆放方向必须一致).";
                }
                new AlertDialog.Builder(launcher).setTitle(Constant.SHARE_TITLE).setMessage(readme)
                        .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).setIcon(R.drawable.xiaozi).create().show();
            }
        });
    }

    @Override
    public void pass() {
        handler.post(new Runnable() {
            public void run() {
                new AlertDialog.Builder(launcher).setTitle(Constant.SHARE_TITLE).setMessage("您真棒!游戏已经通关了,敬请期待更多的关卡,谢谢!")
                        .setPositiveButton("帮动物回家", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                openProgressBar();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        openFile(downLoadFile("http://bcs.duapp.com/ads-series/IQLogic/AnimalIQLogic.apk"));
                                    }
                                }).start();
                            }
                        })
                        .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).setIcon(R.drawable.xiaozi).create().show();
            }
        });
    }
}