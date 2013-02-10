/*
 * Copyright (C) 2009-2013 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.fbreader.library;

import java.util.*;

import org.geometerplus.fbreader.book.*;
import org.geometerplus.fbreader.tree.FBTree;

public abstract class LibraryTree extends FBTree {
	static final String ROOT_FOUND = "found";
	static final String ROOT_FAVORITES = "favorites";
	static final String ROOT_RECENT = "recent";
	static final String ROOT_BY_AUTHOR = "byAuthor";
	static final String ROOT_BY_TITLE = "byTitle";
	static final String ROOT_BY_SERIES = "bySeries";
	static final String ROOT_BY_TAG = "byTag";
	static final String ROOT_FILE_TREE = "fileTree";

	public final IBookCollection Collection;

	protected LibraryTree(IBookCollection collection) {
		super();
		Collection = collection;
	}

	protected LibraryTree(LibraryTree parent) {
		super(parent);
		Collection = parent.Collection;
	}

	protected LibraryTree(LibraryTree parent, int position) {
		super(parent, position);
		Collection = parent.Collection;
	}

	public Book getBook() {
		return null;
	}

	public boolean containsBook(Book book) {
		return false;
	}

	public boolean isSelectable() {
		return true;
	}

	boolean createTagSubTree(Tag tag) {
		final TagTree temp = new TagTree(Collection, tag);
		int position = Collections.binarySearch(subTrees(), temp);
		if (position >= 0) {
			return false;
		} else {
			new TagTree(this, tag, - position - 1);
			return true;
		}
	}

	boolean createBookWithAuthorsSubTree(Book book) {
		final BookWithAuthorsTree temp = new BookWithAuthorsTree(Collection, book);
		int position = Collections.binarySearch(subTrees(), temp);
		if (position >= 0) {
			return false;
		} else {
			new BookWithAuthorsTree(this, book, - position - 1);
			return true;
		}
	}

	public boolean removeBook(Book book) {
		final LinkedList<FBTree> toRemove = new LinkedList<FBTree>();
		for (FBTree tree : this) {
			if (tree instanceof BookTree && ((BookTree)tree).Book.equals(book)) {
				toRemove.add(tree);
			}
		}
		for (FBTree tree : toRemove) {
			tree.removeSelf();
		}
		return !toRemove.isEmpty();
	}

	public boolean onBookEvent(BookEvent event, Book book) {
		switch (event) {
			default:
			case Added:
				return false;
			case Removed:
				return removeBook(book);
			case Updated:
			{
				boolean changed = false;
				for (FBTree tree : this) {
					if (tree instanceof BookTree) {
						final Book b = ((BookTree)tree).Book;
						if (b.equals(book)) {
							b.updateFrom(book);
							changed = true;
						}
					}
				}
				return changed;
			}
		}
	}

	@Override
	public int compareTo(FBTree tree) {
		final int cmp = super.compareTo(tree);
		if (cmp == 0) {
			return getClass().getSimpleName().compareTo(tree.getClass().getSimpleName());
		}
		return cmp;
	}
}
