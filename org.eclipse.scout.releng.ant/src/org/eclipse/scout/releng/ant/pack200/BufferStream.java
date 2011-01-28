/*******************************************************************************
 * Copyright (c) 2010 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.releng.ant.pack200;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** <h4> BufferStream </h4>
 *
 * @author aho
 * @since 1.1.0 (26.01.2011)
 *
 */
public class BufferStream extends OutputStream {
    
    static final int PAGE_SIZE = 4096;
    
    Page head;
    Page cur;
    int curPos;
    
    /**
     * Creates a new instance of BufferStream
     */
    public BufferStream() {
        this.head = new Page();
        this.cur = this.head;
    }
    
    public void write(int value) {
        if(curPos == PAGE_SIZE) {
            newPage();
        }
        cur.buffer[curPos++] = (byte)value;
    }
    
    public void write(byte[] b, int off, int len) {
        while(len > 0) {
            int copyCnt = PAGE_SIZE - curPos;
            if(copyCnt == 0) {
                newPage();
                copyCnt = PAGE_SIZE;
            }
            if(copyCnt > len) {
                copyCnt = len;
            }
            System.arraycopy(b, off, cur.buffer, curPos, copyCnt);
            curPos += copyCnt;
            off += copyCnt;
            len -= copyCnt;
        }
    }
    
    public InputStream getInputStream() {
        return new BufferIS(head, curPos);
    }
    
    private void newPage() {
        cur = cur.next = new Page();
        curPos = 0;
    }
    
    static class Page {
        final byte[] buffer;
        Page next;
        
        Page() {
            this.buffer = new byte[PAGE_SIZE];
        }
    }
    
    static class BufferIS extends InputStream {
        Page cur;
        int lastPageSize;
        int offset;
        
        BufferIS(Page head, int lastPageSize) {
            this.cur = head;
            this.lastPageSize = lastPageSize;
        }

        public int read() throws IOException {
            if(!nextPage()) {
                return -1;
            }
            return cur.buffer[offset++] & 255;
        }

        public int available() throws IOException {
            if(!nextPage()) {
                return 0;
            }
            if(cur.next == null) {
                return lastPageSize - offset;
            }
            return PAGE_SIZE - offset;
        }

        public int read(byte[] b, int off, int len) throws IOException {
            if(len <= 0) {
                return 0;
            }
            int avail = available();
            if(len > avail) {
                len = avail;
            }
            if(len == 0) {
                return -1;
            }
            System.arraycopy(cur.buffer, offset, b, off, len);
            offset += len;
            return len;
        }

        public long skip(long n) throws IOException {
            int skip = (int)Math.min(n, available());
            if(skip > 0) {
                offset += skip;
                return skip;
            }
            return 0;
        }
        
        private boolean nextPage() {
            if(cur != null) {
                if(offset == PAGE_SIZE || (offset == lastPageSize && cur.next == null)) {
                    offset = 0;
                    cur = cur.next;
                }
            }
            return cur != null;
        }
    }
}
