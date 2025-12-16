# RHEL UBI (Red Hat Enterprise Linux, Universal Base Image)

https://rockplace.tistory.com/276

```Dockerfile
FROM registry.access.redhat.com/ubi9:latest

# 사내/추가 CA 인증서 복사 (PEM/CRT)
COPY assets/certs/* /etc/pki/ca-trust/source/anchors/

RUN update-ca-trust extract \
    && dnf install -y \
        glibc-all-langpacks \
    && sed -i 's/^LANG=.*/LANG="ko_KR.utf8"/' /etc/locale.conf \
    && export TZ='Asia/Seoul' \
    && dnf reinstall tzdata -y \
    && dnf clean all
```
