DESCRIPTION = "Resin persistent logs tool"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${RESIN_COREBASE}/COPYING.Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = " \
    file://resin-logs \
    file://resin-logs.service \
    file://resin-prepare-logs.service \
    file://systemd-journald.conf \
    "
S = "${WORKDIR}"

inherit allarch systemd

SYSTEMD_SERVICE_${PN} = " \
    resin-logs.service \
    resin-prepare-logs.service \
    "

RDEPENDS_${PN} = " \
    bash \
    coreutils \
    "

do_install() {
    install -d ${D}${bindir}
    install -d ${D}${sysconfdir}/systemd/system/systemd-journald.service.d/

    install -m 0775 ${WORKDIR}/resin-logs ${D}${bindir}

    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}${systemd_unitdir}/system
        install -c -m 0644 ${WORKDIR}/resin-logs.service ${D}${systemd_unitdir}/system
        install -c -m 0644 ${WORKDIR}/resin-prepare-logs.service ${D}${systemd_unitdir}/system
        install -c -m 0644 ${WORKDIR}/systemd-journald.conf ${D}${sysconfdir}/systemd/system/systemd-journald.service.d/
        sed -i -e 's,@BASE_BINDIR@,${base_bindir},g' \
            -e 's,@SBINDIR@,${sbindir},g' \
            -e 's,@BINDIR@,${bindir},g' \
            ${D}${systemd_unitdir}/system/*.service
    fi
}
