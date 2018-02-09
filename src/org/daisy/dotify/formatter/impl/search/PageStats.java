package org.daisy.dotify.formatter.impl.search;

import org.daisy.dotify.formatter.impl.datatype.VolumeKeepPriority;

public class PageStats {
	private static final PageStats EMPTY = new PageStats(VolumeKeepPriority.empty(), false);
	private final VolumeKeepPriority keepPriority;
	private final boolean hasSuitableBlockTransition;
	
	public PageStats(VolumeKeepPriority keepPriority, boolean trans) {
		this.keepPriority = keepPriority;
		this.hasSuitableBlockTransition = trans;
	}
	
	public VolumeKeepPriority getPrio() {
		return keepPriority;
	}
	
	public boolean trans() {
		return hasSuitableBlockTransition;
	}
	
	public static PageStats empty() {
		return EMPTY;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (hasSuitableBlockTransition ? 1231 : 1237);
		result = prime * result + ((keepPriority == null) ? 0 : keepPriority.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PageStats other = (PageStats) obj;
		if (hasSuitableBlockTransition != other.hasSuitableBlockTransition)
			return false;
		if (keepPriority == null) {
			if (other.keepPriority != null)
				return false;
		} else if (!keepPriority.equals(other.keepPriority))
			return false;
		return true;
	}

}
