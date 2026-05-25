import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StockPorSubUbicacion } from '../../../core/models/stock-report.model';
import { ReporteStockService } from '../../../core/services/reporte-stock.service';


@Component({
  selector: 'app-stock-report',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './stock-report.html',
  styleUrls: ['./stock-report.css']
})
export class StockReport implements OnInit {
  private reporteService = inject(ReporteStockService);

  rawLocationData = signal<StockPorSubUbicacion[]>([]);
  searchTerm = signal('');

  // Lógica de filtrado inteligente (busca en ubicación o producto)
  filteredData = computed(() => {
    const term = this.searchTerm().toLowerCase().trim();
    const data = this.rawLocationData();

    if (!term) return data;

    return data.filter(item => 
      item.tipoUbicacionNombre.toLowerCase().includes(term) || // Ejemplo: "Bodega", "Tienda"
      item.ubicacionNombre.toLowerCase().includes(term)     || // Ejemplo: "Almacén Central"
      item.subUbicacionNombre.toLowerCase().includes(term)     // Ejemplo: "Pasillo A"
    );
  });

  ngOnInit() {
    this.reporteService.getPorSubUbicacion().subscribe(res => {
      this.rawLocationData.set(res);
    });
  }

  exportar(id: number, nombre: string, tipo: 'pdf' | 'excel') {
    const obs = tipo === 'pdf' 
      ? this.reporteService.exportarPdf(id) 
      : this.reporteService.exportarExcel(id);

    obs.subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `Stock-${nombre.replace(/\s+/g, '_')}.${tipo === 'pdf' ? 'pdf' : 'xlsx'}`;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => console.error('Error al exportar reporte', err)
    });
  }
  
}
