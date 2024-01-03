package com.matsinger.barofishserver.global.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class MultiAccessRequestWrapper extends HttpServletRequestWrapper {

    private ByteArrayOutputStream contents = new ByteArrayOutputStream(); // request content를 저장하는 곳 (캐싱)

    // 이 메소드를 통해서 request body의 내용을 inputStream으로 읽는다.
    @Override
    public ServletInputStream getInputStream() throws IOException {
        // request content를 복사
        IOUtils.copy(super.getInputStream(), contents);

        // read를 호출하면 buffer(저장된 내용)을 보내주는 커스텀 ServletInputStream 객체를 생성해서 반환
        return new ServletInputStream() {
            private ByteArrayInputStream buffer = new ByteArrayInputStream(contents.toByteArray());

            @Override
            public int read() throws IOException {
                return buffer.read();
            }

            @Override
            public boolean isFinished() {
                return buffer.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener listener) {
                throw new RuntimeException("Not implemented");
            }
        };
    }

    // contents를 byteArray로 반환
    public byte[] getContents() throws IOException {
        return this.getInputStream().readAllBytes();
    }

    public MultiAccessRequestWrapper(HttpServletRequest request) {
        super(request);
    }
}
