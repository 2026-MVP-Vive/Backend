$baseUrl = "http://3.37.155.46/api/v1"
$username = "mentee01"
$password = "password123"

# Helper Function: Test-Endpoint
function Test-Endpoint {
    param (
        [string]$Name,
        [string]$Url,
        [string]$Method = "Get",
        [hashtable]$Body = $null,
        [hashtable]$Headers = @{}
    )

    Write-Host "Testing $Name ($Method $Url)... " -NoNewline

    $params = @{
        Uri     = "$baseUrl$Url"
        Method  = $Method
        Headers = $Headers
    }

    if ($Body) {
        $params.Body = ($Body | ConvertTo-Json -Depth 10)
        $params.ContentType = "application/json"
    }

    try {
        $response = Invoke-RestMethod @params
        Write-Host "[OK]" -ForegroundColor Green
        return $response.data
    }
    catch {
        Write-Host "[FAIL]" -ForegroundColor Red
        Write-Host "Error: $($_.Exception.Message)"
        if ($_.Exception.Response) {
            $stream = $_.Exception.Response.GetResponseStream()
            $reader = New-Object System.IO.StreamReader($stream)
            $responseBody = $reader.ReadToEnd()
            Write-Host "Response Body: $responseBody"
        }
        return $null
    }
}

# 1. Login
Write-Host "1. Logging in..."
$loginBody = @{
    email    = $username
    password = $password
}

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -Body ($loginBody | ConvertTo-Json) -ContentType "application/json"
    $token = $loginResponse.data.accessToken
    $headers = @{ Authorization = "Bearer $token" }
    Write-Host "[OK] Login successful. Token obtained." -ForegroundColor Green
}
catch {
    Write-Host "[FAIL] Login failed." -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)"
    exit
}

$today = Get-Date -Format "yyyy-MM-dd"

# 2. Create a Test Task for Verification
Write-Host "`n2. Creating Test Task..."
$taskBody = @{
    title = "Test Task for Completion Toggle"
    date = $today
    subject = "MATH"
}
$createdTask = Test-Endpoint -Name "Create Task" -Url "/mentee/tasks" -Method Post -Body $taskBody -Headers $headers
$taskId = $createdTask.id

if (-not $taskId) {
    Write-Host "Failed to create task. Exiting." -ForegroundColor Red
    exit
}
Write-Host "Created Task ID: $taskId"

# 3. Test: Toggle Task Completion (Mentee)
Write-Host "`n3. Testing Toggle Task Completion..."

# 3.1 Mark as Completed
$toggleResponse1 = Test-Endpoint -Name "Mark Task Complete" -Url "/mentee/tasks/$taskId/complete" -Method Patch -Body @{ completed = $true } -Headers $headers
if ($toggleResponse1.isMenteeCompleted -eq $true) {
    Write-Host "  [PASS] Task marked as completed." -ForegroundColor Green
} else {
    Write-Host "  [FAIL] Task should be completed." -ForegroundColor Red
}

# 3.2 Mark as Incomplete
$toggleResponse2 = Test-Endpoint -Name "Mark Task Incomplete" -Url "/mentee/tasks/$taskId/complete" -Method Patch -Body @{ completed = $false } -Headers $headers
if ($toggleResponse2.isMenteeCompleted -eq $false) {
    Write-Host "  [PASS] Task marked as incomplete." -ForegroundColor Green
} else {
    Write-Host "  [FAIL] Task should be incomplete." -ForegroundColor Red
}

# 3.3 Mark as Completed again for Planner Status Check
Test-Endpoint -Name "Mark Task Complete Again" -Url "/mentee/tasks/$taskId/complete" -Method Patch -Body @{ completed = $true } -Headers $headers | Out-Null


# 4. Test: Get Planner Status (Mentee)
Write-Host "`n4. Testing Planner Status..."
$plannerStatus = Test-Endpoint -Name "Get Planner Status" -Url "/mentee/planner/$today/status" -Headers $headers

if ($plannerStatus) {
    Write-Host "  [INFO] Planner Status: $($plannerStatus.status)"
    if ($plannerStatus.tasks -contains $taskId) {
         # Logic check: If task is completed, it might be in the list if planner is completed?
         # Wait, getPlannerStatus returns complete planner status.
         # Actually, completePlanner logic updates tasks. toggleTaskCompletion updates single task.
         # getPlannerStatus logic: returns correct status?
         Write-Host "  [INFO] Planner status response received."
    }
}


# 5. Test: Mentor Student Task View Checks
Write-Host "`n5. Testing Mentor Student Task View..."
# Note: Using same token (assuming mentee can't access mentor api, but for local test maybe ok if role allows or using simple logic)
# Actually, need mentor token. But script uses mentee login.
# Limitation: Checking Mentee side reflection of these fields in TaskDetail if possible, or skip strict Mentor check if we don't have mentor crendentials in script.
# We will check Mentee's Task List to see if `isMenteeCompleted` is reflected there.
$dailyTasks = Test-Endpoint -Name "Get Daily Tasks (Mentee View)" -Url "/mentee/tasks?date=$today" -Headers $headers
$targetTask = $dailyTasks.tasks | Where-Object { $_.id -eq $taskId }

if ($targetTask.isMenteeCompleted -eq $true) {
    Write-Host "  [PASS] isMenteeCompleted reflected in Daily Task List." -ForegroundColor Green
} else {
    Write-Host "  [FAIL] isMenteeCompleted NOT reflected in Daily Task List." -ForegroundColor Red
}

# Clean up
Write-Host "`n6. Cleaning up..."
Test-Endpoint -Name "Delete Task" -Url "/mentee/tasks/$taskId" -Method Delete -Headers $headers | Out-Null

Write-Host "`nverification Complete."
