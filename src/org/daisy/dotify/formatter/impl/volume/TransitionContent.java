package org.daisy.dotify.formatter.impl.volume;

import java.util.Collections;
import java.util.List;

import org.daisy.dotify.formatter.impl.core.Block;
import org.daisy.dotify.formatter.impl.core.FormatterCoreImpl;

public class TransitionContent {
	public enum Type {
		INTERRUPT,
		RESUME
	}
	private final Type type;
	private final List<Block> inBlock;
	private final List<Block> inSeq;

	public TransitionContent(Type type, FormatterCoreImpl inBlock, FormatterCoreImpl inSeq) {
		this.type = type;
		this.inBlock = Collections.unmodifiableList(inBlock);
		this.inSeq = Collections.unmodifiableList(inSeq);
	}

	public Type getType() {
		return type;
	}

	public List<Block> getInBlock() {
		return inBlock;
	}

	public List<Block> getInSequence() {
		return inSeq;
	}
}
