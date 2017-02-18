package com.zhangjikai.linkgame.utils;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

public class BackMusic implements Runnable, MetaEventListener{

	private String midiFile = "music/bg.mid";
	private Sequence sequence = null;
	private Sequencer sequencer;
	private boolean isPlaying = false;
	private volatile Thread thread;
	private static BackMusic back;

	private BackMusic() {
		try {
			loadMidi(midiFile);
		} catch (InvalidMidiDataException ex) {
		} catch (IOException ex) {
		}
	}
	
	public static BackMusic getInstance() {
		if(back == null)
			back = new BackMusic();
		return back;
	}

	/**
	 * 读取midi文件
	 */
	public void loadMidi(String filename) throws IOException, InvalidMidiDataException {
		ClassLoader classLoader = FileHelper.class.getClassLoader();
		sequence = MidiSystem.getSequence(classLoader.getResource(midiFile));
	}

	public void play() {
		if (isPlaying) { // 如果已经在播放，返回
			return;
		}

		try {
			sequencer = MidiSystem.getSequencer();
			sequencer.open();
			sequencer.setSequence(sequence);
			sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY );
			sequencer.addMetaEventListener(this);
		} catch (InvalidMidiDataException ex) {
		} catch (MidiUnavailableException e) {
		}

		thread = new Thread(this);
		thread.start();
	}

	public void stop() {
		if (isPlaying) {
			sequencer.stop();
			isPlaying = false;
		}
		if (thread != null) {
			thread = null;
		}
	}

	public void run() {
		Thread currentThread = Thread.currentThread();
		while (currentThread == thread && !isPlaying) {
			sequencer.start();
			isPlaying = true;
			try {
				Thread.sleep(1000l);
			} catch (InterruptedException ex) {
			}
		}
	}

	public void meta(MetaMessage event) {
		if (event.getType() == 47) {
			System.out.println("Sequencer is done playing.");
		}
	}
}
