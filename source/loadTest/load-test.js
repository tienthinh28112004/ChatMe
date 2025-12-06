const ACCESS_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJUaeG6v24gVGjhu4tuaCIsInN1YiI6IjQ0OGI2MjE4LWE5NTctNDMyMC04ZGRkLTIyNjVhYzFlMDgxMCIsImV4cCI6MTc2NTAwODI4NCwiaWF0IjoxNzY1MDA0Njg0LCJqdGkiOiJhNTY1ZWQ4Yi01MGYzLTRiMGQtOWIzYS1lMWNmYjE2MDBiMjcifQ.PIgBQ8sC9DhvMS4m-_Ak3BqaEvzy9QEYjgnVWA800V9wlaQnVwCHj0LJInATOe2YyhVCeXIrd9uQk-aMyofn2Q";
const ROOM_ID = "0115f5fe-5304-438b-9f15-efceef943e0b";

function generateKey() {
  return "lt_" + Date.now() + "_" + Math.random().toString(36).substring(2);
}

// đếm thành công / thất bại
let successCount = 0;
let failCount = 0;

async function sendOneMessage(i) {
  const idKey = generateKey();

  try {
    const res = await fetch(
      `http://localhost:9000/api/rooms/sendMessage?id=${idKey}`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${ACCESS_TOKEN}`,
        },
        body: JSON.stringify({
          content: "Message " + i,
          messageType: "TEXT",
          roomId: ROOM_ID,
          linkUrl: "",
          fileName: "",
        }),
      }
    );

    if (!res.ok) {
      failCount++;
      console.log(`❌ Error ${i}: status = ${res.status}`);
    } else {
      successCount++;
      console.log(`✔ Sent ${i}`);
    }
  } catch (err) {
    // lỗi mạng, server không trả về, v.v...
    failCount++;
    console.log(`💥 Network error ${i}:`, err);
  }
}

async function runLoadTest(total = 100, batchSize = 20, delayMs = 100) {
  console.log(
    `Running load test: ${total} requests (batchSize=${batchSize}, delay=${delayMs}ms)...`
  );

  successCount = 0;
  failCount = 0;
  const startTime = performance.now?.() ?? Date.now();

  for (let start = 0; start < total; start += batchSize) {
    const end = Math.min(start + batchSize, total);
    const tasks = [];

    for (let i = start; i < end; i++) {
      tasks.push(sendOneMessage(i));
    }

    // batchSize request song song
    await Promise.all(tasks);
    console.log(`✅ Batch ${start} - ${end - 1} done`);

    if (end < total && delayMs > 0) {
      await new Promise((r) => setTimeout(r, delayMs));
    }
  }

  const endTime = performance.now?.() ?? Date.now();
  const durationSec = ((endTime - startTime) / 1000).toFixed(2);

  console.log("🔥 Load test completed!");
  console.log(
    `📊 Tổng: ${total} | Thành công: ${successCount} | Thất bại: ${failCount} | Thời gian: ${durationSec}s`
  );

  return { total, successCount, failCount, durationSec };
}

// chạy lệnh node load-test.js để test
runLoadTest(100, 20, 100);
