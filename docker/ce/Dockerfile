FROM ghcr.io/graalvm/graalvm-ce:ol8-java8-21.1.0
MAINTAINER Karol Wójcik <Karol Wójcik>

RUN microdnf install unzip curl make wget git

ENV CLJ_CACHE=/project/.holy-lambda/clojure
ENV CLJ_COFIG=/project/.holy-lambda/clojure
ENV CLOJURE_TOOLS_DIR=/project/.holy-lambda
ENV HOME=/project/.holy-lambda
ENV XDG_CACHE_HOME=/project/.holy-lambda
ENV XDG_CONFIG_HOME=/project/.holy-lambda
ENV XDG_DATA_HOME=/project/.holy-lambda
ENV GITLIBS=/project/.holy-lambda/.gitlibs/

# --------------- UPDATE ------------------
# -----------------------------------------
ARG BABASHKA_VERSION=0.4.4
ARG CLOJURE_VERSION=1.10.3.849
# -----------------------------------------


# Provide download_pods command
COPY download_pods download_pods.clj /
RUN chmod +x download_pods && mv download_pods /bin/download_pods

# Install babashka
RUN wget -c https://github.com/babashka/babashka/releases/download/v$BABASHKA_VERSION/babashka-$BABASHKA_VERSION-linux-amd64.tar.gz -O - | tar -xz \
    && chmod +x bb \
    && mv bb /bin/bb

# Install Clojure toolbox for babashka
RUN curl https://raw.githubusercontent.com/borkdude/deps.clj/master/install | bash && deps -P

# Install clojure
RUN curl https://download.clojure.org/install/linux-install-$CLOJURE_VERSION.sh | bash

RUN alias java="`which java` -Duser.home=/project/.holy-lambda"
RUN alias bb="`which bb` -Duser.home=/project/.holy-lambda"
RUN alias clojure="`which clojure` -J-Duser.home=\"/project/.holy-lambda\""
RUN alias deps="`which deps` -J-Duser.home=/project/.holy-lambda"

# # Install aws command
# RUN curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip" \
#     && unzip awscliv2.zip -d aws \
#     && ./aws/aws/install \
#     && rm -Rf awscliv2.zip aws

# # Install aws-sam command
# RUN curl -L "https://github.com/aws/aws-sam-cli/releases/latest/download/aws-sam-cli-linux-x86_64.zip" -o "aws-sam.zip" \
#  && unzip aws-sam.zip -d aws-sam \
#  && ./aws-sam/install \
#  && rm -Rf aws-sam aws-sam.zip

RUN gu install native-image

# # BEGIN PRE-REQUISITES FOR STATIC NATIVE IMAGES FOR GRAAL 21.0.0.2
# # Based on https://github.com/oracle/graal/issues/2824
# # SEE: https://github.com/oracle/graal/blob/master/substratevm/StaticImages.md
# #
# # ------------------------------------
# # Credits to @emrul
# # ------------------------------------

# ARG RESULT_LIB="/staticlibs"

# RUN mkdir ${RESULT_LIB} && \
#   curl -L -o musl.tar.gz https://musl.libc.org/releases/musl-1.2.1.tar.gz && \
#   mkdir musl && tar -xvzf musl.tar.gz -C musl --strip-components 1 && cd musl && \
#   ./configure --disable-shared --prefix=${RESULT_LIB} && \
#   make && make install && \
#   cd / && rm -rf /muscl && rm -f /musl.tar.gz

# RUN cp /usr/lib/gcc/x86_64-redhat-linux/8/libstdc++.a ${RESULT_LIB}/lib/

# ENV PATH="$PATH:${RESULT_LIB}/bin"
# ENV CC="musl-gcc"

# RUN curl -L -o zlib.tar.gz https://zlib.net/zlib-1.2.11.tar.gz && \
#   mkdir zlib && tar -xvzf zlib.tar.gz -C zlib --strip-components 1 && cd zlib && \
#   ./configure --static --prefix=${RESULT_LIB} && \
#   make && make install && \
#   cd / && rm -rf /zlib && rm -f /zlib.tar.gz

# # ------------------------------------
# # END PRE-REQUISITES FOR STATIC NATIVE IMAGES FOR GRAAL 21.0.0.2

WORKDIR /project

