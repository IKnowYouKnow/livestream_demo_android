package cn.ucai.live.ucloud;

/**
 * Created by lw.tan on 2017/3/6.
 */

public class AVOption {
    public int videoFramerate = StreamProfileUtil.AVOptionsHolder.DefaultVideoCaptureFps;
    public int videoBitrate = StreamProfileUtil.AVOptionsHolder.DefaultVideoBitrate;
    public int videoResolution = StreamProfileUtil.AVOptionsHolder.DefaultVideoResolution.ordinal();
    public int videoCodecType = StreamProfileUtil.AVOptionsHolder.DefaultVideoCodecType;
    public int videoCaptureOrientation = StreamProfileUtil.AVOptionsHolder.DefaultVideoCaptureOrientation;
    public int audioBitrate = StreamProfileUtil.AVOptionsHolder.DefaultAudioBitrate;
    public int audioChannels = StreamProfileUtil.AVOptionsHolder.DefaultAudioChannels;
    public int audioSampleRate = StreamProfileUtil.AVOptionsHolder.DefaultAudioSamplerate;
    public int videoFilterMode = StreamProfileUtil.AVOptionsHolder.DefaultVideoRenderMode;
    public int cameraIndex = StreamProfileUtil.AVOptionsHolder.DefaultCameraIndex;
    public String streamUrl = "rtmp://publish3.cdn.ucloud.com.cn/ucloud/demo";

    public static final String pullUrl = "rtmp://vlive3.rtmp.cdn.ucloud.com.cn/ucloud/superwechat201612";
    public static final String playUrl = "rtmp://publish3.cdn.ucloud.com.cn/ucloud/superwechat201612";
}