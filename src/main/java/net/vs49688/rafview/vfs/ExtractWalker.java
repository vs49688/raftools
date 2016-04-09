/*
 * RAFTools - Copyright (C) 2015 Zane van Iperen.
 *    Contact: zane@zanevaniperen.com
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2, and only
 * version 2 as published by the Free Software Foundation. 
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Any and all GPL restrictions may be circumvented with permission from the
 * the original author.
 */
package net.vs49688.rafview.vfs;

import java.io.IOException;
import java.nio.file.*;
import static java.nio.file.FileVisitResult.*;
import java.nio.file.attribute.BasicFileAttributes;

public class ExtractWalker implements FileVisitor<Path> {

	private final String m_Version;
	private final Path m_NativePath;
	private final Path m_CurrentDirectory;
	private final RAFS m_VFS;
	
	public ExtractWalker(Path nativePath, Path currentDirectory, String version, RAFS vfs) {
		m_Version = version;
		m_NativePath = nativePath;
		m_CurrentDirectory = currentDirectory.getParent();
		m_VFS = vfs;
	}
	
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		Files.createDirectories(Paths.get(dir.toString()));
		return CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		Path outputPath = m_NativePath.resolve(m_CurrentDirectory.relativize(file).toString());
		
		Path parent = outputPath.getParent();
		Files.createDirectories(parent);
		Files.write(outputPath, m_VFS.getVersionDataForFile(file, m_Version).dataSource.read());
		return CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		System.err.printf("RecursiveDeleteWalker::visitFileFailed(%s): %s\n", file, exc.getMessage());
		exc.printStackTrace(System.err);
		return TERMINATE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		return CONTINUE;
	}
	
}