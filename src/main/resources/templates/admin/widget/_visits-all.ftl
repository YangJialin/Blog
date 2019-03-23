<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <link rel="stylesheet" href="/static/halo-backend/plugins/bootstrap/css/bootstrap.min.css">
</head>
<body>
<div class="container-fluid">
    <div class="row">
        <div class="col-lg-12 attachDesc" style="padding-top: 15px;">
            <div class="box box-solid">
                <div class="box-body table-responsive no-padding">
                    <table class="table table-hover">
                        <tbody>
                            <tr>
                                <th><@spring.message code='admin.logs.th.log-id' /></th>
                                <th>IP</th>
                                <th><@spring.message code='common.th.visit-os' /></th>
                                <th><@spring.message code='common.th.visit-browser' /></th>
                                <th><@spring.message code='common.th.date' /></th>

                            </tr>
                            <#list visits.content as visit>
                                <tr>
                                    <td>${visit.visitId}</td>
                                    <td>${visit.visitIp}</td>
                                    <td>${visit.visitOs!""}</td>
                                    <td>${visit.visitBrowser!""}</td>
                                    <td>${visit.visitTime?string("yyyy-MM-dd HH:mm")}</td>
                                </tr>
                            </#list>
                        </tbody>
                    </table>
                </div>
                <div class="box-footer clearfix">
                    <div class="no-margin pull-left">
                        <@spring.message code='admin.pageinfo.text.no' />${visits.number+1}/${visits.totalPages}<@spring.message code='admin.pageinfo.text.page' />
                    </div>
                    <div class="btn-group pull-right btn-group-sm" role="group">
                        <a class="btn btn-default <#if !visits.hasPrevious()>disabled</#if>" href="/admin/visits">
                            <@spring.message code='admin.pageinfo.btn.first' />
                        </a>
                        <a class="btn btn-default <#if !visits.hasPrevious()>disabled</#if>" href="/admin/visits?page=${visits.number-1}">
                            <@spring.message code='admin.pageinfo.btn.pre' />
                        </a>
                        <a class="btn btn-default <#if !visits.hasNext()>disabled</#if>" href="/admin/visits?page=${visits.number+1}">
                            <@spring.message code='admin.pageinfo.btn.next' />
                        </a>
                        <a class="btn btn-default <#if !visits.hasNext()>disabled</#if>" href="/admin/visits?page=${visits.totalPages-1}">
                            <@spring.message code='admin.pageinfo.btn.last' />
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
<script src="/static/halo-common/jquery/jquery.min.js"></script>
<script src="/static/halo-backend/plugins/bootstrap/js/bootstrap.min.js"></script>
</html>
