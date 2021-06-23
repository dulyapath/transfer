<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

    <head>

        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <meta name="description" content="">
        <meta name="author" content="">

        <title>Transfer System - Login</title>

        <!-- Custom fonts for this template-->
        <link href="vendor/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
        <link
            href="https://fonts.googleapis.com/css?family=Nunito:200,200i,300,300i,400,400i,600,600i,700,700i,800,800i,900,900i"
            rel="stylesheet">

        <!-- Custom styles for this template-->
        <link href="css/sb-admin-2.min.css" rel="stylesheet">

    </head>

    <body class="bg-gradient-primary">

        <div class="container">

            <!-- Outer Row -->
            <div class="row justify-content-center">

                <div class="col-xl-6 col-lg-6 col-md-6" id="tab-login">

                    <div class="card o-hidden border-0 shadow-lg my-5">
                        <div class="card-body p-0">
                            <!-- Nested Row within Card Body -->
                            <div class="row">
                                <div class="col-lg-12">
                                    <div class="p-5">
                                        <div class="text-center">
                                            <h1 class="h4 text-gray-900 mb-4">Transfer System</h1>
                                        </div>
                                        <form class="user">
                                            <div class="form-group">
                                                <input type="text" class="form-control form-control-user"
                                                       id="form-provider" 
                                                       placeholder="Enter Provider...">
                                            </div>
                                            <div class="form-group">
                                                <input type="text" class="form-control form-control-user"
                                                       id="form-plkiz" 
                                                       placeholder="Enter username...">
                                            </div>

                                            <div class="form-group">
                                                <input type="password" class="form-control form-control-user"
                                                       id="form-jzuys" placeholder="Password">
                                            </div>

                                            <button id="btn-login" class="btn btn-primary btn-user btn-block">
                                                เข้าสู่ระบบ
                                            </button>
                                            <hr>
                                            <div id="error" style="margin-top: 10px;"></div>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>
                <div class="col-xs-12 col-md-12 form-box" id="tab-table" style="display:none">
                    <div class="card" style="background-color: #fff;padding: 6px;">
                        <h3 class="card-header text-center">เลือกฐานข้อมูล</h3>
                        <div class="card-body">
                            <table class="table table-hover ">
                                <thead>
                                <th>#</th>
                                <th>ชื่อฐานข้อมูล</th>
                                <th>ชื่อบริษัท</th>
                                </thead>
                                <tbody id="table-detail">

                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

        </div>

        <!-- Bootstrap core JavaScript-->
        <script src="vendor/jquery/jquery.min.js"></script>
        <script src="vendor/bootstrap/js/bootstrap.bundle.min.js"></script>

        <!-- Core plugin JavaScript-->
        <script src="vendor/jquery-easing/jquery.easing.min.js"></script>

        <!-- Custom scripts for all pages-->
        <script src="js/sb-admin-2.min.js"></script>

        <script>
            $(document).ready(function ()
            {
                $('#btn-login').click(function ()
                {
                    console.log('12354')
                    var providerCode = $("#form-provider").val();
                    var plkiz = $("#form-plkiz").val();
                    var jzuys = $("#form-jzuys").val();
                    if ($.trim(providerCode).length > 0 && $.trim(plkiz).length > 0 && $.trim(jzuys).length > 0)
                    {


                        $.post("ajaxlogincontroller", {provider: providerCode, user: plkiz, pass: jzuys}, function (responseText) {
                            //console.log(responseText);
                            if (responseText.success) {
                                if (responseText.type == "1") {

                                    $("#error").html("");
                                    $("#table-detail").html(responseText.data);
                                    $('#form-login').hide();
                                    $('#text-header').text('เลือกฐานข้อมูล');
                                    $('#tab-table').show();
                                    $('#tab-login').hide();
                                } else if (responseText.type == "2") {
                                    window.location.href = 'verify.jsp?database=' + responseText.data;
                                }

                            } else {
                                $("#error").html("<br/><span style='color:#cc0000'>ผิดพลาด :</span> " + responseText.msg);
                                $("#form-plkiz").focus();
                                $("#form-plkiz").select();
                                $("#form-jzuys").val("");
                                $('#form-login').show();
                                $('#tab-table').hide();
                                $('#tab-login').show();
                                $('#text-header').text('เข้าสู่ระบบ');
                            }
                        }).always(function () {

                        });

                    }
                    return false;
                });
            });
        </script>

    </body>

</html>