package com.huawei.hiai.vision.videosummarydemo.videocover.utils;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CutVideoUtils {
    /**
     * 将 MP4 切割
     *
     * @param mp4Path    .mp4
     * @param fromTime 起始位置   不是传入的秒数
     * @param toTime   结束位置   不是传入的秒数
     * @param outPath    .mp4
     */
    public static void cropMp4(String mp4Path, double fromTime, double toTime, String outPath){

        try{
            double videoDuration = getDuration(mp4Path);

            Movie mp4Movie = MovieCreator.build(mp4Path);

            Track videoTracks = null;// 获取视频的单纯视频部分
            for (Track videoMovieTrack : mp4Movie.getTracks()) {
                if ("vide".equals(videoMovieTrack.getHandler())) {
                    videoTracks = videoMovieTrack;
                }
            }

            int totalFrames = videoTracks.getSamples().size();
            double frameRate = totalFrames/videoDuration;

            Track audioTracks = null;// 获取视频的单纯音频部分
            for (Track audioMovieTrack : mp4Movie.getTracks()) {
                if ("soun".equals(audioMovieTrack.getHandler())) {
                    audioTracks = audioMovieTrack;
                }
            }

            long fromSample = (long) (fromTime*frameRate);
            long toSample = (long) (toTime*frameRate);

            Movie resultMovie = new Movie();
            resultMovie.addTrack(new AppendTrack(new CroppedTrack(videoTracks, fromSample, toSample)));// 视频部分
            resultMovie.addTrack(new AppendTrack(new CroppedTrack(audioTracks, fromSample, toSample)));// 音频部分

            Container out = new DefaultMp4Builder().build(resultMovie);
            FileOutputStream fos = new FileOutputStream(new File(outPath));
            out.writeContainer(fos.getChannel());
            fos.close();

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public static double getDuration(String srcVideoPath) throws IOException {

        IsoFile isoFile = new IsoFile(srcVideoPath);  // Error:OutOfMemory
        double lengthInSeconds = (double)
                isoFile.getMovieBox().getMovieHeaderBox().getDuration() /
                isoFile.getMovieBox().getMovieHeaderBox().getTimescale();
        isoFile.close();
        return(lengthInSeconds);
    }
}
