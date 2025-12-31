import { useEffect, useState } from "react";
import axios from "axios";

// ✅ SAFE BASE URL (env → fallback)
const BASE_URL =
  process.env.REACT_APP_API_BASE_URL || "http://localhost:7070";

// 🔎 Debug once
console.log("API BASE URL:", BASE_URL);

function App() {
  // ================= FIREWALL STATE =================
  const [status, setStatus] = useState("Loading...");

  const loadStatus = async () => {
    try {
      const res = await axios.get(`${BASE_URL}/api/firewall/status`, {
        responseType: "text",
      });
      setStatus(res.data);
    } catch (err) {
      console.error("Firewall status error:", err);
      setStatus("ERROR");
    }
  };

  const activate = async () => {
    try {
      const res = await axios.post(
        `${BASE_URL}/api/firewall/activate`,
        {},
        { responseType: "text" }
      );
      setStatus(res.data);
    } catch (err) {
      console.error("Activate firewall error:", err);
      setStatus("ERROR");
    }
  };

  const deactivate = async () => {
    try {
      const res = await axios.post(
        `${BASE_URL}/api/firewall/deactivate`,
        {},
        { responseType: "text" }
      );
      setStatus(res.data);
    } catch (err) {
      console.error("Deactivate firewall error:", err);
      setStatus("ERROR");
    }
  };

  useEffect(() => {
    loadStatus();
  }, []);

  // ================= CSR STATE =================
  const [csrForm, setCsrForm] = useState({
    commonName: "",
    email: "",
    state: "",
    postalCode: "",
    organizationalUnit: "",
    organization: "",
    country: "IN",
    rsaKeySize: "2048",
    hashAlgorithm: "SHA256",
  });

  const [csrOutput, setCsrOutput] = useState("");
  const [csrId, setCsrId] = useState(null);

  const handleCsrChange = (e) => {
    setCsrForm({
      ...csrForm,
      [e.target.name]: e.target.value,
    });
  };

  const resetCsrForm = () => {
    setCsrForm({
      commonName: "",
      email: "",
      state: "",
      postalCode: "",
      organizationalUnit: "",
      organization: "",
      country: "IN",
      rsaKeySize: "2048",
      hashAlgorithm: "SHA256",
    });
    setCsrOutput("");
    setCsrId(null);
  };

  // ================= CSR API =================
  const generateCsr = async () => {
    try {
      const res = await axios.post(
        `${BASE_URL}/api/csr/generate`,
        csrForm,
        { headers: { "Content-Type": "application/json" } }
      );

      setCsrOutput(res.data.csr);
      setCsrId(res.data.id);
    } catch (err) {
      console.error("CSR generation error:", err);
      alert("CSR generation failed");
    }
  };

  return (
    <div style={{ padding: "20px" }}>
      <h1>Cyber Security Dashboard</h1>

      {/* ================= FIREWALL ================= */}
      <h3>Firewall Status: {status}</h3>

      <button onClick={loadStatus}>Check Firewall Status</button>
      <button onClick={activate} style={{ marginLeft: "10px" }}>
        Activate Firewall
      </button>
      <button onClick={deactivate} style={{ marginLeft: "10px" }}>
        Deactivate Firewall
      </button>

      {/* ================= CSR ================= */}
      <hr style={{ margin: "30px 0" }} />
      <h2>CSR Generation</h2>

      <div style={{ maxWidth: "600px" }}>
        <label>Common Name</label>
        <input name="commonName" value={csrForm.commonName} onChange={handleCsrChange} />

        <label>Email</label>
        <input type="email" name="email" value={csrForm.email} onChange={handleCsrChange} />

        <label>State</label>
        <input name="state" value={csrForm.state} onChange={handleCsrChange} />

        <label>Postal Code</label>
        <input name="postalCode" value={csrForm.postalCode} onChange={handleCsrChange} />

        <label>Organizational Unit</label>
        <input name="organizationalUnit" value={csrForm.organizationalUnit} onChange={handleCsrChange} />

        <label>Organization</label>
        <input name="organization" value={csrForm.organization} onChange={handleCsrChange} />

        <label>Country</label>
        <input name="country" value={csrForm.country} onChange={handleCsrChange} />

        <label>RSA Key Size</label>
        <select name="rsaKeySize" value={csrForm.rsaKeySize} onChange={handleCsrChange}>
          <option value="2048">2048</option>
          <option value="3072">3072</option>
          <option value="4096">4096</option>
        </select>

        <label>Hash Algorithm</label>
        <select name="hashAlgorithm" value={csrForm.hashAlgorithm} onChange={handleCsrChange}>
          <option value="SHA256">SHA256</option>
          <option value="SHA384">SHA384</option>
          <option value="SHA512">SHA512</option>
        </select>

        <br /><br />

        <button onClick={generateCsr}>Generate CSR</button>
        <button onClick={resetCsrForm} style={{ marginLeft: "10px" }}>
          Reset
        </button>
      </div>

      {csrOutput && (
        <>
          <h3>Generated CSR</h3>
          <textarea rows="10" cols="80" readOnly value={csrOutput} />

          <br /><br />

          <button
            onClick={() =>
              window.open(`${BASE_URL}/api/csr/${csrId}/download`, "_blank")
            }
          >
            Download CSR as PDF
          </button>
        </>
      )}
    </div>
  );
}

export default App;
