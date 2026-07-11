param(
    [Parameter(Mandatory=$true)]
    [string]$Query
)

# 1. Đọc ConnectDB.properties
$propFile = "src/main/resources/ConnectDB.properties"
if (-not (Test-Path $propFile)) {
    Write-Error "Không tìm thấy file $propFile"
    exit 1
}

$props = @{}
Get-Content $propFile | Where-Object { $_ -match '=' -and -not $_.StartsWith('#') } | ForEach-Object {
    $parts = $_ -split '=', 2
    $props[$parts[0].Trim()] = $parts[1].Trim()
}

$jdbcUrl = $props["url"]
$userId = $props["userID"]
$password = $props["password"]

# Parse JDBC URL để tạo Connection String của ADO.NET
# jdbc:sqlserver://localhost:1433;databaseName=MODA;trustServerCertificate=true
$server = "localhost"
$database = "MODA"

if ($jdbcUrl -match "//([^;:]+)") {
    $server = $Matches[1]
}
if ($jdbcUrl -match "databaseName=([^;]+)") {
    $database = $Matches[1]
}

$connString = "Server=$server;Database=$database;User ID=$userId;Password=$password;Encrypt=True;TrustServerCertificate=True"

# 2. Thực thi câu lệnh SQL
try {
    $connection = New-Object System.Data.SqlClient.SqlConnection($connString)
    $connection.Open()

    $command = $connection.CreateCommand()
    $command.CommandText = $Query

    $adapter = New-Object System.Data.SqlClient.SqlDataAdapter($command)
    $dataset = New-Object System.Data.DataSet
    $adapter.Fill($dataset) | Out-Null

    $connection.Close()

    # In kết quả
    $table = $dataset.Tables[0]
    $table | Format-Table -AutoSize
} catch {
    Write-Error "Lỗi kết nối hoặc truy vấn database: $_"
}
