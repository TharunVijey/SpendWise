
function PlaceholderPage({ title, description }) {
  return (
    <div className="placeholder-page">
      <div className="breadcrumb">Workspace / {title}</div>
      <h1>{title}</h1>
      <p className="subtitle">{description}</p>

      <div className="panel placeholder-panel">
        <div className="placeholder-icon">✦</div>
        <h2>{title} management is coming next!</h2>
        <p>We'll build this page and connect it to your SpendWise backend.</p>
      </div>
    </div>
  )
}

export default PlaceholderPage
